package transport.external;

import common.Employee;
import org.json.JSONArray;
import org.json.JSONObject;
import transport.TransportListenerProxy;
import transport.connector.TransportConnectorClient;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.apache.commons.lang3.SerializationUtils.serialize;

public class TransportExternalListener extends Thread {
    private int serverPort;
    private boolean isStopped;
    private boolean isAccepted;
    private ServerSocket serverSocket;
    private ArrayList<InetSocketAddress> neighbours;
    private String empLocation;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
        if (!isAccepted) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TransportExternalListener(int serverPort, ArrayList<InetSocketAddress> neighbours, String empLocation) {
        this.serverPort = serverPort;
        isStopped = false;
        this.neighbours = neighbours;
        this.empLocation = empLocation;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(serverPort);
            while (!isStopped) {
                Socket socket = serverSocket.accept();  // Blocking call!

                executor.execute(new TransportListenerProxy(
                        socket,
                        neighbours.contains(socket.getInetAddress()),
                        empLocation,
                        neighbours));
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[WARNING] ----------------------------------------- \n" +
                    "[WARNING] Waiting time expired... Socket is closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            executor.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// log4j for printing errors

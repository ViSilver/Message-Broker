package transport;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TransportListener extends Thread {
    private InetSocketAddress address;
    private ServerSocket serverSocket;
    private String empLocation;
    private ArrayList<InetSocketAddress> neighbours;

    public TransportListener(InetSocketAddress address, String empLocation, ArrayList<InetSocketAddress> neighbours) {
        this.address = address;
        this.empLocation = empLocation;
        this.neighbours = neighbours;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(address.getPort());
            Socket sock = serverSocket.accept();
            InputStream in = sock.getInputStream();

            byte[] bytes = new byte[30];
            in.read(bytes);

            String caller = bytes.toString();
            System.out.println(caller);

            sock.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

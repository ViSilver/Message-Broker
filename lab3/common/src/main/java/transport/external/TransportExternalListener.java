package transport.external;

import common.Employee;
import transport.connector.TransportConnectorClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.*;

import static org.apache.commons.lang3.SerializationUtils.serialize;

public class TransportExternalListener extends Thread {
    private int serverPort;
    private boolean isStopped;
    private boolean isAccepted;
    ServerSocket serverSocket;
    private ArrayList<InetSocketAddress> neighbours;

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

    public TransportExternalListener(int serverPort, ArrayList<InetSocketAddress> neighbours) {
        this.serverPort = serverPort;
        isStopped = false;
        this.neighbours = neighbours;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(serverPort);
            while (!isStopped) {
                Socket socket = serverSocket.accept();  // Blocking call!
                // You can use non-blocking approach
                isAccepted = true;
                ArrayList<Employee> arrayEmployees = getEmployees();
                Employee[] employees = new Employee[arrayEmployees.size()];
                serialize((Employee[]) arrayEmployees.toArray(employees), socket.getOutputStream());
                socket.close();
                isAccepted = false;
            }
        } catch (SocketException e) {
            System.out.println("[WARNING] ----------------------------------------- \n" +
                    "[WARNING] Waiting time expired... Socket is closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Employee> getEmployees() {
        BlockingQueue<ArrayList<Employee>> queue = new LinkedBlockingQueue<>();
        ExecutorService clients = Executors.newFixedThreadPool(this.neighbours.size());

        ArrayList<Employee> myEmployees =  new ArrayList<Employee>() {{
            add(new Employee("Laur", "Balaur", "Povesti", 501.0));
//            add(new Employee("Fat", "Frumos", "Basme", 502.0));
//            add(new Employee("Ileana", "Consinzeana", "Basme", 503.0));
//            add(new Employee("Danila", "Prepeleac", "Basme", 304.0));
//            add(new Employee("Ivan", "Turbinca", "Povesti", 505.0));
        }};

        try {
            queue.put(myEmployees);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (InetSocketAddress neighbour : neighbours) {
            clients.execute(new TransportConnectorClient(neighbour, queue));
        }

        try {
            clients.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return removeDuplications(queue);
    }

    private ArrayList<Employee> removeDuplications(BlockingQueue<ArrayList<Employee>> queue) {
        ArrayList<Employee> employees = new ArrayList<>();

        while (!queue.isEmpty()) {
            try {
                ArrayList<Employee> emps = queue.take();
                for (Employee emp : emps) {
                    if (!employees.contains(emp)) {
                        employees.add(emp);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return employees;
    }
}

// log4j for printing errors

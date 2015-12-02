package transport;

import common.Employee;
import common.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

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

            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            String caller = (String) ois.readObject();

            ArrayList<Employee> arrayEmployees = null;

            if (caller.equals("client")) {
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Received employee request from client ...");
                arrayEmployees = getEmployees();

            } else {
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Received employee request from maven ...");
                arrayEmployees = getEmployeesFromFile();
            }

            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(arrayEmployees);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Employee> getEmployees() {
        BlockingQueue<ArrayList<Employee>> queue = new LinkedBlockingQueue<>();
        ExecutorService clients = Executors.newFixedThreadPool(this.neighbours.size());

        ArrayList<Employee> myEmployees =  new ArrayList<Employee>() {{
            add(new Employee("Laur", "Balaur", "Povesti", 501.0));
        }};

        try {
            queue.put(myEmployees);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (InetSocketAddress neighbour : neighbours) {
            clients.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList<Employee> emps =
                                new TransportClient().getEmployeesFrom(new Location(neighbour), "maven");
                        queue.put(emps);
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        try {
            clients.awaitTermination(10, TimeUnit.SECONDS);
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

    private ArrayList<Employee> getEmployeesFromFile() {
        String strFile = "";
        ArrayList<Employee> emps = new ArrayList<>();

        try{
            strFile = new Scanner(new File(empLocation)).useDelimiter("\\Z").next();
            JSONObject json = new JSONObject(strFile);
            JSONArray arr = (JSONArray) json.get("employees");

            for (int i = 0; i < arr.length(); i++) {
                emps.add(new Employee((JSONObject) ((JSONObject) arr.get(i)).get("Employee")));
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return emps;
    }
}

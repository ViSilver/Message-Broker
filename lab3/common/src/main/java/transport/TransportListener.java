package transport;

import common.Employee;
import common.Employees;
import common.Location;
import serialization.JsonSerializer;
import serialization.XMLSerializer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
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
    private boolean isStopped = false;

    public TransportListener(InetSocketAddress address, String empLocation, ArrayList<InetSocketAddress> neighbours) {
        this.address = address;
        this.empLocation = empLocation;
        this.neighbours = neighbours;
    }

    public void setStopped (boolean state) {isStopped = state;}

    @Override
    public void run() {

        try {
            while (!isStopped) {
                serverSocket = new ServerSocket(address.getPort());
                Socket sock = serverSocket.accept();

                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                String caller = (String) ois.readObject();

                ArrayList<Employee> arrayEmployees = null;

                if (caller.equals("client")) {
                    System.out.println("[INFO] -----------------------------------------\n" +
                            "[INFO] Received employee request from client ...");
                    arrayEmployees = getEmployees();

                    OutputStream os = new FileOutputStream(
                            new File("employees_" + Integer.toString(address.getPort()) + ".xml")
                    );

                    Employees listEmployees = new Employees(arrayEmployees);
                    XMLSerializer xmlSerializer = new XMLSerializer();
                    xmlSerializer.serialize(listEmployees, sock.getOutputStream());

                } else if (caller.equals("maven")){
                    System.out.println("[INFO] -----------------------------------------\n" +
                            "[INFO] Received employee request from maven ...");
                    arrayEmployees = getEmployeesFromFile();

                    Employees listEmployees = new Employees(arrayEmployees);
                    JsonSerializer jsonSerializer = new JsonSerializer();
                    jsonSerializer.serialize(listEmployees, sock.getOutputStream());
                }

//                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
//                oos.writeObject(arrayEmployees);

                sock.close();
            }

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
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

package transport;


import common.Employee;
import org.json.JSONArray;
import org.json.JSONObject;
import transport.connector.TransportConnectorClient;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.apache.commons.lang3.SerializationUtils.serialize;

public class TransportListenerProxy extends Thread {
    private Socket socket;
    private boolean isMaven;
    private String empLocation;
    private ArrayList<InetSocketAddress> neighbours;

    public TransportListenerProxy(Socket socket,
                                  boolean isMaven,
                                  String empLocation,
                                  ArrayList<InetSocketAddress> neighbours) {
        this.socket = socket;
        this.isMaven = isMaven;
        this.empLocation = empLocation;
        this.neighbours = neighbours;
    }

    @Override
    public void run() {
        try {
            if (isMaven) {
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Received employee request from client ...");
                ArrayList<Employee> arrayEmployees = getEmployees();
                Employee[] employees = new Employee[arrayEmployees.size()];
                serialize((Employee[]) arrayEmployees.toArray(employees), socket.getOutputStream());
                socket.close();
            } else {
                System.out.println("[INFO] -----------------------------------------\n" +
                        "[INFO] Received employee request from maven ...");
                ArrayList<Employee> arrayEmployees = getEmployeesFromFile();
                Employee[] employees = new Employee[arrayEmployees.size()];
                serialize((Employee[]) arrayEmployees.toArray(employees), socket.getOutputStream());
                socket.close();
            }
        } catch (IOException ex) {
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

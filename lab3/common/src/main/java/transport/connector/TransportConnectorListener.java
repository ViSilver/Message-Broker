package transport.connector;

import common.Employee;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.apache.commons.lang3.SerializationUtils.serialize;

public class TransportConnectorListener extends Thread {
    private InetSocketAddress address;
    private boolean isAccepted;
    ServerSocket serverSocket;
    String empLocation;

    public TransportConnectorListener(InetSocketAddress address, String empLocation) {
        this.address = address;
        this.empLocation = empLocation;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(address.getPort());
            Socket socket = serverSocket.accept();

            isAccepted = true;
            ArrayList<Employee> arrayEmployees = getEmployeesFromFile();
            Employee[] employees = new Employee[arrayEmployees.size()];
            serialize((Employee[]) arrayEmployees.toArray(employees), socket.getOutputStream());
            socket.close();
            isAccepted = false;

        } catch (SocketException e) {
            System.out.println("[WARNING] ----------------------------------------- \n" +
                    "[WARNING] Waiting time expired... Socket is closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

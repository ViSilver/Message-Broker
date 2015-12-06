package transport;


import common.Employee;
import common.Employees;
import common.Location;
import serialization.JsonSerializer;
import serialization.XMLSerializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class TransportClient {

    public ArrayList<Employee> getEmployeesFrom(Location location, String caller) throws IOException {
        Socket sock = new Socket();
        sock.connect(location.getLocation());

        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
        oos.writeObject(caller);

//        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
//        ArrayList<Employee> employees = null;
//        try {
//            employees = (ArrayList<Employee>) ois.readObject();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


        Employees emps = null;

        if (caller.equals("client")) {
            XMLSerializer xmlSerializer = new XMLSerializer();
            emps = xmlSerializer.deserialize(sock.getInputStream());
        } else if (caller.equals("maven")) {
            JsonSerializer jsonSerializer = new JsonSerializer();
            emps = jsonSerializer.deserialize(sock.getInputStream());
        }

        sock.close();

        return emps;
    }
}

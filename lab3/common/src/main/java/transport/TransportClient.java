package transport;


import common.Employee;
import common.Employees;
import common.Location;
import serialization.JsonSerializer;
import serialization.XMLSerializer;
import serialization.XSDValidator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TransportClient {

    public ArrayList<Employee> getEmployeesFrom(Location location, String caller) throws IOException {
        Socket sock = new Socket();
        sock.connect(location.getLocation());

        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
        oos.writeObject(caller);

        Employees emps = null;

        if (caller.equals("client")) {
            XMLSerializer xmlSerializer = new XMLSerializer();
            emps = xmlSerializer.deserialize(sock.getInputStream());
            OutputStream os = new FileOutputStream(
                    new File("employees_" + Integer.toString(location.getLocation().getPort()) + ".xml")
            );
            xmlSerializer.serialize(emps, os);

            boolean isValid = XSDValidator.validateXMLSchema(
                    "schema.xsd",
                    "employees_" + Integer.toString(location.getLocation().getPort()) + ".xml");

            System.out.println("[INFO] -----------------------------------------------\n" +
                    "[INFO]Is the given XML validated? -> " + Boolean.toString(isValid));

        } else if (caller.equals("maven")) {
            JsonSerializer jsonSerializer = new JsonSerializer();
            emps = jsonSerializer.deserialize(sock.getInputStream());
        }

        sock.close();

        return emps;
    }
}

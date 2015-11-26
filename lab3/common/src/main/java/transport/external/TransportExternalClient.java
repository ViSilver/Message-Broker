package transport.external;

import common.Employee;
import common.Location;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.commons.lang3.SerializationUtils.deserialize;

public class TransportExternalClient {

    public ArrayList<Employee> getEmployeesFrom(Location location) throws IOException {
        Socket socket = new Socket();
        socket.connect(location.getLocation());
        Employee[] employees = (Employee[]) deserialize(socket.getInputStream());
        socket.close();
        return new ArrayList<Employee>(Arrays.asList(employees));
    }
}

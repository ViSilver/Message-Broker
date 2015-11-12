package transport.connector;

import common.Employee;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static org.apache.commons.lang3.SerializationUtils.deserialize;

public class TransportConnectorClient extends Thread{
    InetSocketAddress neighbour;
    BlockingQueue<ArrayList<Employee>> queue;

    public TransportConnectorClient(InetSocketAddress neighbour, BlockingQueue<ArrayList<Employee>> queue) {
        this.neighbour = neighbour;
        this.queue = queue;
    }

    @Override
    public void run(){
        Employee[] employees;
        Socket socket = new Socket();

        try {
            socket.connect(neighbour);
            employees = (Employee[]) deserialize(socket.getInputStream());
            socket.close();
            queue.put(new ArrayList<Employee>(Arrays.asList(employees)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

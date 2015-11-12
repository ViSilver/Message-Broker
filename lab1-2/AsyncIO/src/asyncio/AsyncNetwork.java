package asyncio;

import asyncio.IAsyncIO;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AsyncNetwork implements IAsyncIO {

    private DatagramSocket datagramSocket;
    private ExecutorService executor;
    private int port;

    public AsyncNetwork(int port, int poolSize, ExecutorService executor) throws IOException {
        // if you don't specify the host, it takes the localhost
        datagramSocket = new DatagramSocket(); 
        this.executor = executor;
    }

    public Future<String> asyncRead(String location) throws InterruptedException {

        System.out.println("Reading async from network");
        
        Future<String> data;
        data = executor.submit(new Callable<String>() {
            public String call() throws Exception {
                // code for the connection
                byte[] buffer = new byte[2 ^ 16];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                String data = new String(buffer);
                System.out.println("Inside receiving through network");
                
//                try {
//                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//
//                    dbFactory.setIgnoringComments(true);
//                    dbFactory.setIgnoringElementContentWhitespace(true);
//                    dbFactory.setValidating(true);
//
//                    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
//
//                    Document doc = (Document) dbBuilder.parse(data);
//
//                    return doc;
//
//                } catch (ParserConfigurationException | SAXException | IOException ex) {
//                    System.out.println(ex.getMessage());
//                }
                
                return data;
            }
        });
        return data;
    }

    public void asyncWrite(String data) {
        
        // Future<?> fu = executor.submit(new Callable<Void> {});
        Future<?> doc;
        doc = executor.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                byte[] buffer = data.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), port);
                System.out.println("Inside sending through network");
                datagramSocket.send(packet);
                return null;
            }  
        });
    }

    @Override
    public void printString() {

    }
}

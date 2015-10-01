package iasyncio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkIO implements IAsyncIO {
    
    private DatagramSocket datagramSocket;
//    private ExecutorService executor;
    private final int port;
    
    public NetworkIO(int port){
        this.port = port;
//        this.executor = executor;
    }

    @Override
    public String asyncRead(String location) {
        String strNet = "";
        
        try {
            this.datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[2048];
            
            while (true)  {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                strNet = new String(buffer);
                
                // save the data from the request app (request.getAddress(), request.getPort())
                
                datagramSocket.close();
                
                return strNet;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(NetworkIO.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
        return strNet;
    }

    @Override
    public void asyncWrite(String location, String data) {
        
        final String data1 = "(" + location + ")" + data;
        
        
                try {
                    datagramSocket = new DatagramSocket();
                    
                    byte[] buffer = data1.getBytes();
                    System.out.println("Buffer length: " + buffer.length);
                    InetAddress host = InetAddress.getByName("localhost");
            
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, port);
            
                    datagramSocket.send(packet);
            
                } catch (IOException ex) {
                    Logger.getLogger(NetworkIO.class.getName()).log(Level.SEVERE, null, ex);
                }   
    }
    
}

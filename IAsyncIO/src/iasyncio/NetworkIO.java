package iasyncio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;

public class NetworkIO implements IAsyncIO {
    
    private DatagramSocket datagramSocket;
//    private ExecutorService executor;
    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
    
    
    @Override
    public Message read(String location) {
        Message message = new Message();
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
                
                // parse the input string
                // set the corresponding type and body/params
                
                return message;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(NetworkIO.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
        return message;
    }

    @Override
    public void write(String location, Message mess) {
       
        try {
            datagramSocket = new DatagramSocket();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
            objOutStream.writeObject(mess);
            byte[] buffer = outStream.toByteArray();
            
            InetAddress host = InetAddress.getByName("localhost");
            
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, port);

            datagramSocket.send(packet);

        } catch (IOException ex) {
            Logger.getLogger(NetworkIO.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
}

package receiver;

import iasyncio.FileIO;
import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import utils.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App2 {

    private static BlockingQueue<Message> queMessage = new LinkedBlockingQueue();
    private static BlockingQueue<Message> queFile = new LinkedBlockingQueue();
    private static int messCounter = 0;
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
//    private static ExecutorService execFile = Executors.newCachedThreadPool();
            
    public static void main(String[] args){
        
        NetworkIO netWrite = new NetworkIO();
        netWrite.setPort(3000);
        
        Runnable listener;
        listener = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                NetworkIO netRead = new NetworkIO();
                netRead.setPort(3002);
                
                Message message = new Message();
                String subdata;
           
                while (true) {
                    try {
                        message = netRead.read("localhost");
                        int index = message.getBody().indexOf(")");
                        int length = message.getBody().length();
                        subdata = message.getBody().substring(index + 1, length);
                        message.setBody(subdata);
                        queMessage.put(message);
                        System.out.println("Received message");
//                        break;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
//        String str = net.asyncRead("localhost");
        
        Runnable sender = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                NetworkIO netWrite = new NetworkIO();
                netWrite.setPort(3000);
                
                // take the message from the queue
                Message message = new Message();
                
                message.setType("message");
                message.setBody("(subscribe)(app:2)(port:3002)");
                
                while(true) {
                    try {
                        message = queFile.take();
                        System.out.println("Sending message");
                        netWrite.write("App2", message);
//                    Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }   
                }
//                        int index = message.indexOf(")");
//                        int length = message.length();
//                        message = message.substring(index + 1, length);
//                        queue.put(message);
            }
        };
        
        Runnable reader = new Runnable(){

            @Override
            public void run() {
                IAsyncIO fileRead = new FileIO();
                
                Message message = fileRead.read("src/sender/input.xml");
                
                try {
                    queFile.put(message);
                } catch (InterruptedException ex) {
                    Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        Runnable writer = new Runnable() {

            @Override
            public void run() {
                
                IAsyncIO fileWrite = new FileIO(); 
                while(true){
                    try {
                        Message message = queMessage.take();
                        messCounter++;
                        String location = "src/receiver/mess" + messCounter + ".xml";
                        fileWrite.write(location, message);
                        System.out.println("Receiver received and wrote to file:\n" + message);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        executor.submit(listener);
        executor.submit(writer);   
                
//        String str;
//        try {
//            str = (String) queMessage.take();
//            System.out.println("Receiver received: " + str);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        // sending and receiving messages must be asynchronously
        
    }
}

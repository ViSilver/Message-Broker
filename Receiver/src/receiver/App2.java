package receiver;

import iasyncio.FileIO;
import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App2 {

    private static BlockingQueue queMessage = new LinkedBlockingQueue();
    private static BlockingQueue queFile = new LinkedBlockingQueue();
    private static int messCounter = 0;
//    private static ExecutorService execNet = Executors.newCachedThreadPool();
//    private static ExecutorService execFile = Executors.newCachedThreadPool();
            
    public static void main(String[] args){
        
        IAsyncIO netWrite;
        netWrite = new NetworkIO(3000);
        
        Thread listener;
        listener = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                IAsyncIO netRead = new NetworkIO(3002);
                String message = "";
           
                while (true) {
                    try {
                        message = "";
                        message = netRead.read("localhost");
                        int index = message.indexOf(")");
                        int length = message.length();
                        message = message.substring(index + 1, length);
                        queMessage.put(message);
                        System.out.println("Received message");
//                        break;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
//        String str = net.asyncRead("localhost");
        
        Thread sender = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                IAsyncIO netWrite = new NetworkIO(3000);
                
                // take the message from the queue
                String message = "";
                
                try {
                    message = (String) queFile.take();
                    System.out.println("Sending message");
                    netWrite.write("App2", message);
//                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                }               
//                        int index = message.indexOf(")");
//                        int length = message.length();
//                        message = message.substring(index + 1, length);
//                        queue.put(message);
            }
        });
        
        Thread reader = new Thread(new Runnable(){

            @Override
            public void run() {
                IAsyncIO fileRead = new FileIO();
                
                String data = fileRead.read("src/sender/input.xml");
                
                try {
                    queFile.put(data);
                } catch (InterruptedException ex) {
                    Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        Thread writer = new Thread(new Runnable() {

            @Override
            public void run() {
                
                IAsyncIO fileWrite = new FileIO(); 
                while(true){
                    try {
                        String data = (String) queMessage.take();
                        messCounter++;
                        String location = "src/receiver/mess" + messCounter + ".xml";
                        fileWrite.write(location, data);
                        System.out.println("Receiver received and wrote to file:\n" + data);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        listener.start();
        writer.start();
        
        
        
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

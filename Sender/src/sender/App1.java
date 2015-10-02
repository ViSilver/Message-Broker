package sender;

import iasyncio.FileIO;
import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App1 {
    
    private static BlockingQueue queMessage = new LinkedBlockingQueue();
    private static BlockingQueue queFile = new LinkedBlockingQueue();
    private static int messCounter = 0;
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
//    private static ExecutorService execFile = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException{
        //listens to the port 3001
        
        Runnable listener = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                IAsyncIO netRead = new NetworkIO(3001);
                String message = "";
                
                try {
                    while(true) {
                        message = netRead.read("localhost");
                        //send confirmation back
                        int index = message.indexOf(")");
                        int length = message.length();
                        message = message.substring(index + 1, length);
                        queMessage.put(message);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        // implement a thread which will send several messages to other aplications
        
        Runnable sender = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                IAsyncIO netWrite = new NetworkIO(3000);
                
                // take the message from the queue
                String message = "";
                
                message = "(subscribe)(app:1)(port:3001)";
                // try to send the message
                
                while(true){
                    try {
                        message = (String) queFile.take();
                        System.out.println("Sending message");
                        netWrite.write("App2", message);
//                    Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
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
                
                String data = fileRead.read("src/sender/input.xml");
                
                try {
                    queFile.put(data);
                } catch (InterruptedException ex) {
                    Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        Runnable writer = new Runnable() {

            @Override
            public void run() {
                
                IAsyncIO fileWrite = new FileIO(); 
                while(true){
                    try {
                        String data = (String) queMessage.take();
                        messCounter++;
                        String location = "src/sender/mess" + messCounter + ".xml";
                        fileWrite.write(location, data);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        executor.submit(reader);
        executor.submit(listener);
        executor.submit(sender);
        executor.submit(writer);
        // sending and receiving messages must be asynchronously performed
       
    }
}

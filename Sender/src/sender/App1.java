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
    
    private static BlockingQueue queue1 = new LinkedBlockingQueue();
    private static BlockingQueue queue2 = new LinkedBlockingQueue();
    private static ExecutorService execNet = Executors.newCachedThreadPool();
    private static ExecutorService execFile = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException{
        //listens to the port 3001
        
        Thread listener = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                IAsyncIO netRead = new NetworkIO(3001, execNet);
                String message = "";
                
                try {
                    while(true) {
                        message = netRead.asyncRead("localhost");
                        //send confirmation back
                        int index = message.indexOf(")");
                        int length = message.length();
                        message = message.substring(index + 1, length);
                        queue1.put(message);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // implement a thread which will send several messages to other aplications
        
        Thread sender = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                IAsyncIO netWrite = new NetworkIO(3000, execNet);
                
                // take the message from the queue
                String message = "Hello from sender.";
                
                System.out.println("Sending message");
                netWrite.asyncWrite("App2", message);
//                        int index = message.indexOf(")");
//                        int length = message.length();
//                        message = message.substring(index + 1, length);
//                        queue.put(message);
            }
        });
        
        Thread reader = new Thread(new Runnable(){

            @Override
            public void run() {
                IAsyncIO fileRead = new FileIO(execFile);
                
            }
            
        });
        
        listener.start();
        sender.start();    
        // sending and receiving messages must be asynchronously performed
        
    }
    
}

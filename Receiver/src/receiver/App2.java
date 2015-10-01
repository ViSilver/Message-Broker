package receiver;

import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App2 {

    private static BlockingQueue queue1 = new LinkedBlockingQueue();
    private static BlockingQueue queue2 = new LinkedBlockingQueue();
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
                
                try {
                    while(true) {
                        message = "";
                        message = netRead.asyncRead("localhost");
                        int index = message.indexOf(")");
                        int length = message.length();
                        message = message.substring(index + 1, length);
                        queue1.put(message);
                        System.out.println("Received message");
//                        break;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
//        String str = net.asyncRead("localhost");
        
        Thread sender = new Thread(new Runnable() {

            @Override
            public void run() {
                // create asynchronous threads
            }
            
        });
        
        listener.start();
        
        
        
        String str;
        try {
            str = (String) queue1.take();
            System.out.println("Receiver received: " + str);
        } catch (InterruptedException ex) {
            Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // sending and receiving messages must be asynchronously
        
    }
}

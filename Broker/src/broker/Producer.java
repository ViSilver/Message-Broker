package broker;

import asyncio.AsyncFile;
import asyncio.AsyncNetwork;
import asyncio.IAsyncIO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer implements Runnable{
    
    private BlockingQueue queue;
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    Producer(BlockingQueue q){
        queue = q;
    }

    @Override
    public void run() {
        // listen to the messages
        IAsyncIO netRead = new AsyncNetwork(3000, executor);
        IAsyncIO fileRead = new AsyncFile(executor);
        String message = "";
                
        try {
            while(true) {
                message = netRead.asyncRead("localhost");
                queue.put(message);
                // send confirmation message
                System.out.println("Inserting the message into the queue: " + message);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

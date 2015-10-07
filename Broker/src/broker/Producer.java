package broker;

import iasyncio.FileIO;
import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;
import utils.Subscriber;

public class Producer implements Runnable{
    
    private BlockingQueue<Message> queue;
//    private BlockingQueue<Subscriber> subscribers = new LinkedBlockingQueue<Subscriber>();
//    private ExecutorService executor = Executors.newCachedThreadPool();
    
    Producer(BlockingQueue q){
        queue = q;
    }

    @Override
    public void run() {
        // listen to the messages
        NetworkIO netRead = new NetworkIO();
        netRead.setPort(3000);
        IAsyncIO fileRead = new FileIO();
        
        Message mess;
                
        try {
            while(true) {
                mess = netRead.read("localhost");
                System.out.println("Inserting the message into the queue: " + mess);
                // here is the deadlock
                queue.put(mess);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

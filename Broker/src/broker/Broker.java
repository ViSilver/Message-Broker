package broker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Broker {
    
    static BlockingQueue queue = new LinkedBlockingQueue();

    public static void main(String[] args) throws InterruptedException {
        
        Thread listener;
        listener = new Thread(new Producer(queue));
        
        Thread resender1 = new Thread(new Consumer(queue));
        
        // it will be better to create two classes: producer and consumer
        
        Thread resender2 = new Thread(new Consumer(queue));
        
        listener.start();
        resender1.start();
//        resender2.start();
        
//        listener.join();
        
    }
    
}
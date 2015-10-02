package broker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Broker {
    
    static BlockingQueue queue = new LinkedBlockingQueue();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException {
        
        Runnable listener = new Producer(queue);
        
        Runnable resender1 = new Consumer(queue);
        
        // it will be better to create two classes: producer and consumer
        
        Runnable resender2 = new Consumer(queue);
        
        executor.submit(listener);
        executor.submit(resender1);
//        resender2.start();
        
//        listener.join();
        
    }
    
}
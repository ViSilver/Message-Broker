package broker;

import utils.Subscriber;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import utils.Message;
import utils.Subscriber;

public class Broker {
    
    static BlockingQueue<Message> queue = new LinkedBlockingQueue();
    static BlockingQueue<Subscriber> subscribers = new LinkedBlockingQueue();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException {
        
        Runnable listener = new Producer(queue);
        Runnable resender1 = new Consumer(queue, subscribers);
        Runnable resender2 = new Consumer(queue, subscribers);
        
        executor.submit(listener);
        executor.submit(resender1);
        executor.submit(resender2);
    }
}
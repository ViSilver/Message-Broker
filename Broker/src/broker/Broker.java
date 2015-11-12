package broker;

import utils.Subscriber;
import utils.MessageFile;
import utils.Message;
import utils.Subscriber;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Broker {
    
    static BlockingQueue<Message> queue = new LinkedBlockingQueue();
    static BlockingQueue<Subscriber> subscribers = new LinkedBlockingQueue();
    static BlockingQueue<MessageFile> messFiles = new LinkedBlockingQueue();
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static boolean replica = false;
    private final static int REPLICA_PORT = 2999;
    private final static int MASTER_PORT = 3000;
    
    public Broker(boolean replica){
        this.replica = replica;
    }

    public static void main(String[] args) throws InterruptedException {
        
        Runnable listener;
        if(replica){
            // replica listener
            listener = new Producer(queue, REPLICA_PORT, replica);
        } else {
            // master listener
            listener = new Producer(queue, MASTER_PORT, replica);
        }
        
        Runnable resender1 = new Consumer(queue, subscribers, messFiles, replica);
        Runnable resender2 = new Consumer(queue, subscribers, messFiles, replica);
        
        Runnable messageChecker = new MessageChecker(messFiles);
        
        Thread list = new Thread(listener);
        list.start();
        executor.submit(resender1);
        executor.submit(resender2);
        executor.submit(messageChecker);
    }
}
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

    public static void main(String[] args) throws InterruptedException {
        
        Runnable listener = new Producer(queue);
        Runnable resender1 = new Consumer(queue, subscribers, messFiles);
        Runnable resender2 = new Consumer(queue, subscribers, messFiles);
        Runnable messageChecker = new MessageChecker(messFiles);
        
        Thread list = new Thread(listener);
        list.start();
        executor.submit(resender1);
        executor.submit(resender2);
        executor.submit(messageChecker);
    }
}
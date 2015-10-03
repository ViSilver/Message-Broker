package sender;

import iasyncio.FileIO;
import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import utils.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App1 {
    
    private static BlockingQueue<Message> queMessage = new LinkedBlockingQueue();
    private static BlockingQueue<Message> queFile = new LinkedBlockingQueue();
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
                Message message = new Message();
                String mess = "";
                
                try {
                    while(true) {
                        message = netRead.read("localhost");
                        //send confirmation back
                        int index = mess.indexOf(")");
                        int length = mess.length();
                        mess = mess.substring(index + 1, length);
                        message.setBody(mess);
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
                Message message = new Message();
                String[] params = new String[2];
                
                message.setType("subscribe");
                params[0] = "app:1";
                params[1] = "port:3001"; 
                message.setParams(params);
                // try to send the subscription message 
                
                while(true){
                    try {
                        message = queFile.take();
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
                
                Message mess = fileRead.read("src/sender/input.xml");
                
                try {
                    queFile.put(mess);
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
                        Message mess = queMessage.take();
                        messCounter++;
                        String location = "src/sender/mess" + messCounter + ".xml";
                        fileWrite.write(location, mess);
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

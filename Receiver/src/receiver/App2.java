package receiver;

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

public class App2 {

    private static BlockingQueue<Message> queMessage = new LinkedBlockingQueue();
    private static BlockingQueue<Message> queFile = new LinkedBlockingQueue();
    private static int messCounter = 0;
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
            
    public static void main(String[] args){
        
        NetworkIO netWrite = new NetworkIO();
        netWrite.setPort(3000);
        
        Runnable listener = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                NetworkIO netRead = new NetworkIO();
                netRead.setPort(3002);
                
                Message message = new Message();
                Message confirmation = new Message();
           
                while (true) {
                    try {
                        message = netRead.read("localhost");
                        queMessage.put(message);
                        System.out.println("Received message");
                        
                        String[] params = message.getParams();
                        
                        confirmation.setType("deliv_conf");
                        String[] confParams = new String[2];
                        confParams[0] = params[2]; // mess id
                        confParams[1] = "Broker"; // sender id (sending back to it)
                        confirmation.setParams(confParams);
                        
                        netWrite.write("Broker", confirmation);
                        
//                        break;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        Runnable sender = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                NetworkIO netWrite = new NetworkIO();
                netWrite.setPort(3000);
                
                // take the message from the queue
                Message message = new Message();
                String[] params = new String[3];
                
                message.setType("subscribe");
                params[0] = "App2";
                params[1] = "localhost";
                params[2] = "3002";
                message.setParams(params);
                
                netWrite.write("Broker", message);
                
                System.out.println("Sending subscription");
                
                while(true) {
                    try {
                        message = queFile.take();
//                
                        params = new String[3];
                        params[0] = "App2";     // From
                        params[1] = "App1";     // To
                        params[2] = "0";        // ID of message
                
                        message.setParams(params);
                        
                        System.out.println("Sending message");
                        netWrite.write("App2", message);
//                    Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }   
                }
            }
        };
        
        Runnable reader = new Runnable(){

            @Override
            public void run() {
                IAsyncIO fileRead = new FileIO();
                
//                Message message = fileRead.read("src/receiver/input.xml");
//                
//                try {
//                    queFile.put(message);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
            }
        };
        
        Runnable writer = new Runnable() {

            @Override
            public void run() {
                
                IAsyncIO fileWrite = new FileIO(); 
                while(true){
                    try {
                        Message mess = queMessage.take();
                        if(!mess.getType().equals("deliv_conf")){
                            messCounter++;
                            String location = "src/sender/mess" + messCounter + ".xml";
                            fileWrite.write(location, mess);
                        } else {
                            System.out.println("Message " + mess.getParams()[0] + " was confirmed");
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        executor.submit(listener);
        executor.submit(writer); 
        executor.submit(sender);
        executor.submit(reader);
    }
}

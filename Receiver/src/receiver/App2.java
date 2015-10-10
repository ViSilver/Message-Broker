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
import utils.DeliveryConfirmationParameter;
import utils.MessageParameter;
import utils.SubscribtionParameter;

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
                        
                        MessageParameter messParam = (MessageParameter) message.getParams();
                        
                        confirmation.setType("deliv_conf");
                        
                        DeliveryConfirmationParameter confParam = new DeliveryConfirmationParameter();
                        confParam.setMess_id(messParam.getMess_id());
                        confParam.setSender("Broker");
                        confirmation.setParams(confParam);
                        
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
                SubscribtionParameter subscrParam = new SubscribtionParameter();
                MessageParameter messParam;
                
                message.setType("subscribe");
                
                subscrParam.setApp_id("App2");
                subscrParam.setIp("localhost");
                subscrParam.setPort(3002);
                
                message.setParams(subscrParam);
                
                netWrite.write("Broker", message);
                
                System.out.println("Sending subscription");
                
                while(true) {
                    try {
                        message = queFile.take();
//                
                        messParam = new MessageParameter();
                        messParam.setSender_id("App2");
                        messParam.setReceiver_id("App1");
                        messParam.setMess_id("0");
                
                        message.setParams(messParam);
                        
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
                            System.out.println("Message " + 
                                    ((DeliveryConfirmationParameter) mess.getParams()).getMess_id() 
                                    + " was confirmed");
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

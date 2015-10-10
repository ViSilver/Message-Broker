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
import utils.DeliveryConfirmationParameter;
import utils.MessageParameter;
import utils.SubscribtionParameter;

public class App1 {
    
    private static BlockingQueue<Message> queMessage = new LinkedBlockingQueue();
    private static BlockingQueue<Message> queFile = new LinkedBlockingQueue();
    private static int messCounter = 0;
    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws InterruptedException{
        //listens to the port 3001 
        NetworkIO netWrite = new NetworkIO();
        netWrite.setPort(3000);
        
        Runnable listener = new Runnable() {
            
            @Override
            public void run() {
                // listen to the messages
                NetworkIO netRead = new NetworkIO();
                netRead.setPort(3001);
                Message message = new Message();
                Message confirmation = new Message();
                
                try {
                    while(true) {
                        message = netRead.read("localhost");
                        //send confirmation back
                        queMessage.put(message);
                        
                        MessageParameter messParam = new MessageParameter();
                        
                        confirmation.setType("deliv_conf");
                        
                        DeliveryConfirmationParameter confParams = new DeliveryConfirmationParameter();
                        confParams.setMess_id(messParam.getMess_id());
                        confParams.setSender("Broker");
                        confirmation.setParams(confParams);
                        
                        netWrite.write("Broker", confirmation);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        Runnable sender = new Runnable() {
            
            @Override
            public void run() {
                NetworkIO netWrite = new NetworkIO();
                netWrite.setPort(3000);
                
                Message message = new Message();
                SubscribtionParameter subscrParam = new SubscribtionParameter();
                MessageParameter messParam;
                
                message.setType("subscribe");
                subscrParam.setApp_id("App1");
                subscrParam.setIp("127.0.0.1");
                subscrParam.setPort(3001);
                message.setParams(subscrParam);
                // try to send the subscription message 
                
                netWrite.write("Broker", message);
                
                System.out.println("Sending subscription");
                
                while(true){
                    try {
                        message = queFile.take();
                        
                        messParam = new MessageParameter();
                        messParam.setSender_id("App1");
                        messParam.setReceiver_id("App2");
                        messParam.setMess_id("0");
                
                        message.setParams(messParam);
                        
                        System.out.println("Sending message");
                        netWrite.write("App2", message);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App1.class.getName()).log(Level.SEVERE, null, ex);
                    }    
                }
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
                        if(!mess.getType().equals("deliv_conf")){
                            messCounter++;
                            String location = "src/sender/mess" + messCounter + ".xml";
                            fileWrite.write(location, mess);
                        } else {
                            System.out.println("Message " + ((MessageParameter) mess.getParams()).getMess_id() + " was confirmed");
                        }
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
    }
}

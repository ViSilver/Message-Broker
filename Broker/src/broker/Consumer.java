package broker;

import iasyncio.FileIO;
import utils.Subscriber;
import iasyncio.NetworkIO;
import utils.Message;
import utils.MessageFile;
import utils.DeliveryConfirmationParameter;
import utils.MessageParameter;
import utils.PingParameter;
import utils.SubscribtionParameter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class Consumer implements Runnable {
    
    
//    String[] getParam(String s) {
//        
//        String[] params = new String[2];
//        
//        int index = s.indexOf(")");
//        int length = s.length();
//        String param = s.substring(1, index);
//        String rest = s.substring(index + 1, length);
//        params[0] = param;
//        params[1] = rest;
//                
//        return params;
//    }
    
    void subscribeApp(SubscribtionParameter params){
        
        String appName = params.getAppID();
        String ip = params.getIp();
        int port = params.getPort();
        
        Subscriber rcvr = new Subscriber(appName, port, ip);
        // store it into the list of receivers
        try {
//            System.out.println("Received a subscription from: " + appName);
            this.subscribers.put(rcvr);
//            System.out.println("Subscribed app: " + rcvr.name);
        } catch (InterruptedException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    synchronized void sendMessage(Message mess, NetworkIO netWriter){
        // create a netWrite obj according to "to" param
        String to = ((MessageParameter) mess.getParams()).getReceiverID();
        Iterator<Subscriber> it = this.subscribers.iterator();
        
        if(this.subscribers.isEmpty()){
            System.out.println("The subs q is empty");
        }
        
        while(it.hasNext()){
            Subscriber sub = it.next();
            System.out.println(sub.name);
            if(to.equals(sub.name)){
                netWriter.setPort(sub.port);
                netWriter.write(to, mess);
                System.out.println("Resending the message to " + to);
                return;
            }
        }
        
        // save the message into a file
        System.out.println("The receiver wasn't found.");
    }
    
    
    void pingResponse(String data){
        // it receives a string of the form
        // "(from:id,port2)"
    }
    
    
    void changeMessDelivStatus(DeliveryConfirmationParameter param){
        
        String messID = param.getMessageID();
        String senderID = param.getSenderID();
        
        Iterator<MessageFile> it = messFiles.iterator();
        
        while(it.hasNext()){
            MessageFile messFile = it.next();
            MessageParameter messParam = messFile.getParams();
            
            if(messParam.getSenderID().equals(senderID) 
                    && messParam.getMessID().equals(messID)){
                messFile.setDelivered(true); // this might not work
            }
        }
    }
    

    private BlockingQueue<Message> queMessage;
    private BlockingQueue<Subscriber> subscribers;
    private BlockingQueue<MessageFile> messFiles;
    private ExecutorService fileWriter = Executors.newCachedThreadPool();
    
    
    Consumer(BlockingQueue q, BlockingQueue subs, BlockingQueue mF) {
        queMessage = q;
        subscribers = subs;
        messFiles = mF;
    }
    
    
    @Override
    public void run() {
        Message message = new Message();
        MessageParameter messParam;
        DeliveryConfirmationParameter delivConfParam;
        SubscribtionParameter subscribParam;
        PingParameter pingParam;
        NetworkIO netWriter = new NetworkIO();
        FileIO fileWrite = new FileIO();
           
        while(true){
            try {
//          Thread.sleep(10000);
                message = queMessage.take();
//                System.out.println("Retrieving from message queue");
            } catch (InterruptedException ex) {
                Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //implement some logic for processing the resending
            switch(message.getType()){
                case "subscribe":
                    subscribParam = (SubscribtionParameter) message.getParams();
                    subscribeApp(subscribParam);
                    break;
                    
                case "mess":
                    messParam = (MessageParameter) message.getParams();
                    System.out.println("Received a message: " + messParam.getMessID());
                    sendMessage(message, netWriter);
                    
                    Message confirmation = new Message();
                    confirmation.setType("deliv_conf");
                    
                    DeliveryConfirmationParameter confParam = new DeliveryConfirmationParameter();
                    confParam.setMessageID(messParam.getMessID());
                    confParam.setSenderID(messParam.getSenderID());
                    confirmation.setParams(confParam);
                    
//                    System.out.println("here");
                    
                    {
                        try {
                            queMessage.put(confirmation);
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    MessageFile messFile = new MessageFile();
                    messFile.setParams(messParam);
                    messFile.setDelivered(false);
                    
                    final String filePath = "src/broker/" + messParam.getSenderID() 
                            + "_" + messParam.getMessID() + ".xml";
                    
                    final Message copyMessage = message;
                    
                    Callable<Void> callable;
                    callable = new Callable<Void>() {

                        @Override
                        public Void call() throws Exception {
                            
                            fileWrite.write(filePath, copyMessage);
                            return null;
                            
                        }
                    };
                    
                    Future<Void> futureFile = fileWriter.submit(callable);
                    
                    messFile.setFilePath(filePath);
                    messFile.setFileWrite(futureFile);
                    break;
                    
                case "ping":
                    // the broker replicat sends a ping
                    // to see if this instance is working
                    pingParam = (PingParameter) message.getParams();
                    pingResponse(pingParam.getSenderID());
                    break;
                    
                case "pong": 
                    // receives a pong from the main broker
                    {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                    
                case "deliv_conf":
                    // received a delivery confirmation
                    // from app about a sent message
                    DeliveryConfirmationParameter param = (DeliveryConfirmationParameter) message.getParams();
                    
                    if(!param.getSenderID().equals("Broker")){
                        System.out.println("Deliver confirmation for: " + param.getMessageID());
                        sendMessage(message, netWriter);
                    } else {
                        System.out.println("The message " + param.getMessageID() + " was delivered");
//                        sendMessage(message, netWriter);
                    }
                    changeMessDelivStatus(param); // it needs to send a MessageFile obj
                    break;
                    
                default:
                    break;
            }
        }
    }
}

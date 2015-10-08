package broker;

import utils.Subscriber;
import iasyncio.NetworkIO;
import utils.Message;
import utils.MessageFile;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;

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
    
    void subscribeApp(String[] params){
        
        String appName = params[0];
        String ip = params[1];
        int port = Integer.parseInt(params[2]);
        
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
        String to = mess.getParams()[1];
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
    
    
    void changeMessDelivStatus(String data){
        
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
        String[] params;
        NetworkIO netWriter = new NetworkIO();
           
        while(true){
            try {
//          Thread.sleep(10000);
                message = queMessage.take();
//                System.out.println("Retrieving from message queue");
            } catch (InterruptedException ex) {
                Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //implement some logic for processing the resending
            
            params = message.getParams();
            switch(message.getType()){
                case "subscribe":
                    subscribeApp(params);
                    break;
                    
                case "mess":
                    System.out.println("Received a message: " + params[1]);
                    sendMessage(message, netWriter);
                    
                    Message confirmation = new Message();
                    confirmation.setType("deliv_conf");
                    String[] confParams = new String[2];
                    confParams[0] = params[2]; // mess id
                    confParams[1] = params[0]; // sender id (sending back to it)
                    confirmation.setParams(confParams);
                    System.out.println("here");
                    
                    {
                        try {
                            queMessage.put(confirmation);
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    break;
                    
                case "ping":
                    // the broker replicat sends a ping
                    // to see if this instance is working
                    pingResponse(params[1]);
                    break;
                    
                case "response": 
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
                    if(!message.getParams()[1].equals("Broker")){
                        System.out.println("Deliver confirmation for: " + message.getParams()[0]);
                        sendMessage(message, netWriter);
                    } else {
                        System.out.println("The message " + message.getParams()[0] + " was delivered");
                    }
                    changeMessDelivStatus(params[1]);
                    break;
                    
                default:
                    break;
            }
        }
    }
}

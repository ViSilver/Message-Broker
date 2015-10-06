package broker;

import iasyncio.NetworkIO;
import utils.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer implements Runnable {
    
    private class Subscriber {
        public String name;
        public int port;
        public String IP;
        
        Subscriber(String name, int port, String ip){
            this.name = name;
            this.port = port;
            this.IP = ip;
        }
    }
    
    
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
    
    
    synchronized void subscribeApp(String[] params){
        
        String appName = params[0];
        String ip = params[1];
        int port = Integer.parseInt(params[2]);
        
        Subscriber rcvr = new Subscriber(appName, port, ip);
        // store it into the list of receivers
        try {
            System.out.println("Received a subscription from: " + appName);
            subscribers.put(rcvr);
        } catch (InterruptedException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Subscribed app: " + rcvr.name);
    }
    
    
    void sendMessage(Message mess, NetworkIO netWriter){
        // create a netWrite obj according to "to" param
        String to = mess.getParams()[1];
        
        for(Subscriber subscriber : this.subscribers){
            System.out.println(subscriber.name);
            if(to.equals(subscriber.name)){
                netWriter.setPort(subscriber.port);
                netWriter.write(to, mess);
                System.out.println("Resending the message to" + to);
                return;
            }
        }
        
        System.out.println("The receiver wasn't found.");
    }
    
    
    void pingResponse(String data){
        // it receives a string of the form
        // "(from:id,port2)"
    }
    
    
    void changeMessDelivStatus(String data){
        
    }
    

    private BlockingQueue<Message> queMessage;
    private BlockingQueue<Subscriber> subscribers = new LinkedBlockingQueue<Subscriber>();
    private static final Object mutex = new Object();
    
    Consumer(BlockingQueue q) {
        queMessage = q;
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
                // all the switch must be inside try
            } catch (InterruptedException ex) {
                Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //implement some logic for processing the resending
            
            params = message.getParams();
            switch(message.getType()){
                case "subscribe":
                    // broker receives a subsribe message 
                    // from an app
                    
                    
                        
                    subscribeApp(params);
                    
                    break;
                    
                case "mess":
                    System.out.println("Received a message: " + message.getParams()[1]);
                    sendMessage(message, netWriter);
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
                    
                case "received":
                    // received a delivery confirmation
                    // from app about a sent message
                    changeMessDelivStatus(params[1]);
                    break;
                    
                default:
                    break;
            }
            
            String receiver = params[1];
       
                
//            IAsyncIO netWrite;
                
//            switch(receiver) {
//                case "App1":
//                    netWrite = new NetworkIO(3001);
//                    break;
//                        
//                case "App2":
//                    netWrite = new NetworkIO(3002);
//                    break;
//                        
//                case "App3":
//                    netWrite = new NetworkIO(3003);
//                    break;
//                        
//                case "App4":
//                    netWrite = new NetworkIO(3004);
//                    break;
//                        
//                default:
//                    netWrite = new NetworkIO(3001);
//                    break;
//            }
            
            // create a thread for each resending
//            netWrite.write(receiver, message);
        }
    }
}

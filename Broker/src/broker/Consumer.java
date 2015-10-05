package broker;

import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import utils.Message;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
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
    
    
    String[] getParam(String s) {
        
        String[] params = new String[2];
        
        int index = s.indexOf(")");
        int length = s.length();
        String param = s.substring(1, index);
        String rest = s.substring(index + 1, length);
        params[0] = param;
        params[1] = rest;
                
        return params;
    }
    
    
    void subscribeApp(String data){
        // it receives a string of the form:
        // "(app:id1)(port:3002)(ip:127.0.0.1)"
        
        String[] tmp = getParam(data); 
        String id = tmp[0].split(":")[1];
        
        tmp = getParam(tmp[1]);
        int port = Integer.parseInt(tmp[0].split(":")[1]);
        
        tmp = getParam(tmp[1]);
        String ip = tmp[0].split(":")[1];
        
        Subscriber rcvr;
        rcvr = new Subscriber(id, port, ip);
        // store it into the list of receivers
        
        subscribers.add(rcvr);
    }
    
    
    void sendMessage(String data, NetworkIO netWriter){
        // it receives a string of the form:
        // "(from:id1)(to:id1,id2)message"
        Message mess = new Message();
        
        String tmp[] = getParam(data);
        String from = tmp[0].split(":")[1];
        
        tmp = getParam(tmp[1]);
        String to = tmp[0].split(":")[1];
        
        mess.setBody(tmp[1]);
        mess.setType("mess");
        
        String[] params = new String[2];
        params[0] = from;
        params[1] = to;
        
        mess.setParams(params);
        
        // create a netWrite obj according to "to" param
        
        for(Subscriber subscriber : this.subscribers){
            if(subscriber.name == to){
                netWriter.setPort(subscriber.port);
                netWriter.write(to, mess);
            }
        }
    }
    
    
    void pingResponse(String data){
        // it receives a string of the form
        // "(from:id,port2)"
    }
    
    
    void changeMessDelivStatus(String data){
        
    }
    

    private BlockingQueue<Message> queMessage;
    private ArrayList<Subscriber> subscribers = new ArrayList<Subscriber>();
//    private ExecutorService executor = Executors.newCachedThreadPool();
    
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
                System.out.println("Resending");
                // all the switch must be inside try
            } catch (InterruptedException ex) {
                Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //implement some logic for processing the resending
            
            params = message.getParams();
            switch(params[0]){
                case "subscribe":
                    // broker receives a subsribe message 
                    // from an app
                    subscribeApp(params[1]);
                    break;
                    
                case "mess":
                    sendMessage(params[1], netWriter);
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
       
                
            IAsyncIO netWrite;
                
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

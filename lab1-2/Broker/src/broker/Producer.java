package broker;

import iasyncio.FileIO;
import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import utils.Message;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.MessageParameter;

public class Producer implements Runnable{
    
    private BlockingQueue<Message> queue;
    private int port;
    private boolean replica;
    
    Producer(BlockingQueue q, int port, boolean replica){
        queue = q;
        this.port = port;
        this.replica = replica;
    }

    @Override
    public void run() {
        // listen to the messages
        NetworkIO netRead = new NetworkIO();
        netRead.setPort(this.port);
        IAsyncIO fileRead = new FileIO();
        
        Message mess;
                
        try {
            while(true) {
                mess = netRead.read("localhost");
                if(!mess.getType().equals("change_port")){
                    queue.put(mess);
                } else {
                    this.port = 3000;
                    netRead.setPort(port);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Broker.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
}

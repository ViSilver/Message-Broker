package broker;

import iasyncio.NetworkIO;
import iasyncio.IAsyncIO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer implements Runnable{
    
    private BlockingQueue queue;
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    Consumer(BlockingQueue q) {
        queue = q;
    }

    @Override
    public void run() {
        String message = "";
           
        while(true){
            try {
//          Thread.sleep(10000);
                message = (String) queue.take();
                System.out.println("Resending");
            } catch (InterruptedException ex) {
                Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                //implement some logic for processing the resending
            int index = message.indexOf(")");
            int length = message.length();
            String receiver = message.substring(1, index);
            message = message.substring(index + 1, length);
                
            IAsyncIO netWrite;
                
            switch(receiver) {
                case "App1":
                    netWrite = new NetworkIO(3001, executor);
                    break;
                        
                case "App2":
                    netWrite = new NetworkIO(3002, executor);
                    break;
                        
                case "App3":
                    netWrite = new NetworkIO(3003, executor);
                    break;
                        
                case "App4":
                    netWrite = new NetworkIO(3004, executor);
                    break;
                        
                default:
                    netWrite = new NetworkIO(3001, executor);
                    break;
            }
                
            netWrite.asyncWrite(receiver, message);
        }
    }
}

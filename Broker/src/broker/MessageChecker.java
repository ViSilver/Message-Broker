package broker;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import utils.MessageFile;


public class MessageChecker implements Runnable{
    
    private BlockingQueue<MessageFile> messFiles;

    public MessageChecker(BlockingQueue<MessageFile> messFile) {
        this.messFiles = messFile;
    }

    @Override
    public void run() {
        
        while(true){
            
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            
            Iterator<MessageFile> it = messFiles.iterator();
            
            while(it.hasNext()){
                MessageFile messF = it.next();
                if(messF.isDelivered()){
                    File file = new File(messF.getFilePath());
                    file.delete(); 
                }
            }
        }
        
    }
}

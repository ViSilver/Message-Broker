package iasyncio;

import utils.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileIO implements IAsyncIO {
    
    public FileIO() {}
    
    @Override
    public Message read(String location){
                
        String strFile = "";
        try{
            strFile = new Scanner(new File(location)).useDelimiter("\\Z").next();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        Message m = new Message();
        m.setType("mess");
        m.setBody(strFile);
        
        return m;
        
    }

    @Override
    public void write(String location, Message mess) {
        
        try {
            byte[] fileArray = mess.getBody().getBytes();
            Path newFilePath = Paths.get(location);
            Path parentDir = newFilePath.getParent();
            if(!Files.exists(parentDir)){
                Files.createDirectories(parentDir);
            }
            Files.write(newFilePath, fileArray, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package iasyncio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileIO implements IAsyncIO {
    
    public FileIO() {
    }
    
    @Override
    public String asyncRead(String location){
                
        String strFile = "";
        try{
            strFile = new Scanner(new File(location)).useDelimiter("\\Z").next();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return strFile;
        
    }

    @Override
    public void asyncWrite(String location, String data) {
        
        try {
            PrintWriter out = new PrintWriter(location);
            out.print(data);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

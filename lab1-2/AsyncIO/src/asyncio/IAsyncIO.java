package asyncio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.w3c.dom.Document;

public interface IAsyncIO {
    
    public Future<String> asyncRead(String location) throws InterruptedException;
    
    public void asyncWrite(String data);
    
    public void printString();
    
}

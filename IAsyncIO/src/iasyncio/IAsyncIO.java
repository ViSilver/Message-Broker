package iasyncio;


public interface IAsyncIO {
    
    public String read(String location);
    
    public void write(String location, String data);
}


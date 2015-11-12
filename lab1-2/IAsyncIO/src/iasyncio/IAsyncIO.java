package iasyncio;

import utils.Message;


public interface IAsyncIO {
    
    public Message read(String location);
    
    public void write(String location, Message data);
}


package asyncio;


public interface IAsyncIO {
    
    public String asyncRead(String location);
    
    public void asyncWrite(String location, String data);
}

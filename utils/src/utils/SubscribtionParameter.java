package utils;

public class SubscribtionParameter extends Parameter{
    
    private String appID;
    private String ip;
    private int port;

    public String getAppID() {
        return appID;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    } 
}

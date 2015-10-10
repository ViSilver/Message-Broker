package utils;

public class SubscribtionParameter extends Parameter{
    
    private String app_id;
    private String ip;
    private int port;

    public String getApp_id() {
        return app_id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    } 
}

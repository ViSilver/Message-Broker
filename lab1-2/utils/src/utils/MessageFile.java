package utils;

import java.util.concurrent.Future;

public class MessageFile {
    
    private boolean delivered;
    private MessageParameter params;
    private String filePath;
    private Future<Void> fileWrite;

    public boolean isDelivered() {
        return delivered;
    }

    public MessageParameter getParams() {
        return params;
    }

    public String getFilePath() {
        return filePath;
    }

    public Future<Void> getFileWrite() {
        return fileWrite;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public void setParams(MessageParameter params) {
        this.params = params;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileWrite(Future<Void> fileWrite) {
        this.fileWrite = fileWrite;
    }
}

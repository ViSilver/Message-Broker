package utils;

public class MessageParameter extends Parameter{

    private String messID;
    private String senderID;
    private String receiverID;

    public void setMessID(String messID) {
        this.messID = messID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessID() {
        return messID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }
}

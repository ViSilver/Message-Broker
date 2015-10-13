package utils;

public class DeliveryConfirmationParameter extends Parameter{
    
    private String MessageID;
    private String senderID;

    public String getMessageID() {
        return MessageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setMessageID(String MessageID) {
        this.MessageID = MessageID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}

package utils;

public class DeliveryConfirmationParameter extends Parameter{
    
    private String mess_id;
    private String sender;

    public String getMess_id() {
        return mess_id;
    }

    public String getSender() {
        return sender;
    }

    public void setMess_id(String mess_id) {
        this.mess_id = mess_id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

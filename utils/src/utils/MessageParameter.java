package utils;

public class MessageParameter extends Parameter{

    private String mess_id;
    private String sender_id;
    private String receiver_id;

    public void setMess_id(String mess_id) {
        this.mess_id = mess_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMess_id() {
        return mess_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }
}

package FlashChatServer;

import java.util.Date;

public class Message {
    private String msgID;
    private String text;
    private String senderId;
    private String recipient_id ;
    private Date date ;
    private int read;
    private int type;

    public Message(String msgID, String text, String senderId, String recipient_id, Date date, int read, int type) {
        this.msgID = msgID;
        this.text = text;
        this.senderId = senderId;
        this.recipient_id = recipient_id;
        this.date = date;
        this.read = read;
        this.type = type;
    }
}


import java.io.Serializable;
import java.time.Instant;

/**
 * This class is a sendable message object, further information may be added
 */

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private Object payload;
    private Instant time;
    private String type; // may be an enum
    private int sequenceNo;
    /**
     * Default - Constructor
     */
    public Message () {
        this.time = Instant.now();
        this.sequenceNo = -1;
    }


    /* GETTER - SETTER */

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
}
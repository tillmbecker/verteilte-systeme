import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    private String text;
    private Timestamp timestamp;

    public Message (String text) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }
}


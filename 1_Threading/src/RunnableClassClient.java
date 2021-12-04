import java.io.IOException;

public class RunnableClassClient implements Runnable {
    Client client = new Client("localhost", 9876);

    public RunnableClassClient() {
    }

    public void run() {
        try {
            client.connect();
            client.sendMessages();
            client.requestLastMessage();
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

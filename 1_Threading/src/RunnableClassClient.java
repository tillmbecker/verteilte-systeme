import java.io.IOException;

public class RunnableClassClient implements Runnable {
    Client client = new Client("localhost", 9876);

    public RunnableClassClient() throws IOException {
    }

    public void run() {
        try {
            client.connect();
            client.sendMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.io.IOException;

public class RunnableClassServer implements Runnable {
    Server server = new Server(9876);

    public RunnableClassServer() throws IOException {
    }

    public void run() {
        try {
            server.connect();
            server.receiveMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

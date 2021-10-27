import java.io.IOException;

public class RunnableClassServer implements Runnable {
    Server server = new Server(9876);

    public RunnableClassServer() {
    }

    public void run() {
        try {
            server.connect();
            server.delegateConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

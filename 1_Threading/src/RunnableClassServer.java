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

// Socket Knoten mappen
// Lokal im Prozess merken welche Sockets man kennt
// Also follower nachricht an den Leader schicken -> "Ich bin ein Follower und das ist mein Port"
// Leader merkt sich die Liste
// Schickt eine aktuelle Liste mit accept an den Follower


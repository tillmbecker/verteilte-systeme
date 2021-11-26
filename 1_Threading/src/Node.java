import java.net.Socket;

public class Node {
    private int port;
    private boolean master;
    private Socket socket;

    public Node(int port, boolean master) {
        this.port = port;
        this.master = master;
        this.socket = socket;
    }

    public Socket getSocket() { return socket;}

    public int getPort() {
        return port;
    }

    public boolean isMaster() {
        return master;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
}

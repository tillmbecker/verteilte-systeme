public class Node {
    private int port;
    private boolean master;

    public Node(int port, boolean master) {
        this.port = port;
        this.master = master;
    }

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

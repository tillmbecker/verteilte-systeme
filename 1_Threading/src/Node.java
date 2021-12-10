import java.io.Serializable;
import java.net.Socket;

public class Node implements Serializable {
    private int portClient;
    private boolean isMaster;
    private Socket socketReference;

    public Node(int portClient, boolean isMaster, Socket socketReference) {
        this.portClient = portClient;
        this.isMaster = isMaster;
        this.socketReference = socketReference;
    }

    public int getPortClient() {
        return portClient;
    }

    public void setPortClient(int portClient) {
        this.portClient = portClient;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public Socket getSocketReference() {
        return socketReference;
    }

    public void setSocketReference(Socket socketReference) {
        this.socketReference = socketReference;
    }
}

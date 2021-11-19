import java.io.IOException;

public class RunnableClassSlave implements Runnable {
    Slave slave = new Slave(9006, "localhost", 9876);

    public RunnableClassSlave() {
    }

    public void run() {
        try {
            slave.connect();
            slave.connecttoMaster();
            slave.delegateConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

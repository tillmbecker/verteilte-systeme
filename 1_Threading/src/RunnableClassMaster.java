import java.io.IOException;

public class RunnableClassMaster implements Runnable {
    Master master;

    public RunnableClassMaster(int port) {
        master = new Master(port);
    }

    public void run() {
        try {
            master.start();
            master.delegateConnections();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}

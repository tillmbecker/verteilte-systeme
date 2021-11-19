import java.io.IOException;

public class RunnableClassMaster implements Runnable {
    Master master = new Master(9876);

    public RunnableClassMaster() {
    }

    public void run() {
        try {
            master.start();
            master.delegateConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

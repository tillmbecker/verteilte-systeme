public class RunnableClassSlave implements Runnable {
private Slave slave;
    public RunnableClassSlave(int slavePort) {
        slave = new Slave(slavePort, "localhost", 9876);
    }

    public void run() {
        try {
            slave.start();
            slave.connectToMaster();
            slave.delegateConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

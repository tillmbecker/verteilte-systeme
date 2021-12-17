public class RunnableClassSlave implements Runnable {
private Slave slave;
    public RunnableClassSlave(int slavePort, int masterPort) {
        slave = new Slave(slavePort, "localhost", masterPort);
    }

    public void run() {
        try {
            slave.connectToMaster();
            slave.createClientHandler();
            slave.delegateConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class RunnableClassSlave implements Runnable {
    private int amountOfSlaves;
    private int slavePort;
    private String masterHost;
    private int masterPort;


    public RunnableClassSlave(int amountOfSlaves, int slavePort, String masterHost, int masterPort) {
        this.slavePort = slavePort;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.amountOfSlaves = amountOfSlaves;

    }

    public void run() {
        Slave slave = new Slave(slavePort, masterHost, masterPort);
        try {
            slave.connectToMaster();
            slave.createClientHandler();
            slave.delegateConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

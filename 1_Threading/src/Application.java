import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        int masterPort = 9000;
        int startSlavePort = 8000;
        int numberOfSlaves = 5;
        int amountOfPrimes = 1000;

        RunnableClassMaster runnableClassMaster = new RunnableClassMaster(masterPort);
        RunnableClassSlave runnableClassSlave;
        Thread runner;
        runner = new Thread(runnableClassMaster);
        runner.start();

        for (int i = 0; i < numberOfSlaves; i++) {
            runnableClassSlave = new RunnableClassSlave(2, startSlavePort + i, "localhost", masterPort);
            runner = new Thread(runnableClassSlave);
            runner.start();
        }
        RunnableClassClient runnableClassClient = new RunnableClassClient("localhost", startSlavePort, amountOfPrimes);
        runner = new Thread(runnableClassClient);
        runner.start();
//        runner.join();
    }
}
import java.io.IOException;

public class Application {
    public static void main(String [] args) throws InterruptedException, IOException {

        RunnableClassMaster runnableClassMaster = new RunnableClassMaster();
        RunnableClassSlave runnableClassSlave = new RunnableClassSlave(9001);
        RunnableClassSlave runnableClassSlave2 = new RunnableClassSlave(9002);
        RunnableClassClient runnableClassClient = new RunnableClassClient();
//        RunnableClassClient runnableClassClient2 = new RunnableClassClient();
        Thread runner1 = new Thread(runnableClassMaster);
        Thread runner2 = new Thread(runnableClassSlave);
        Thread runner4 = new Thread(runnableClassSlave2);
        Thread runner3 = new Thread(runnableClassClient);
//        Thread runner4 = new Thread(runnableClassClient2);
//        RunnableClassServer runnableObjectServer = new RunnableClassServer();
//        RunnableClassClient runnableObjectClient = new RunnableClassClient();
//        RunnableClassClient runnableObjectClient2 = new RunnableClassClient();
//        RunnableClassClient runnableObjectClient3 = new RunnableClassClient();
//        Thread runner1 = new Thread(runnableObjectServer);
//        Thread runner2 = new Thread(runnableObjectClient);
//        Thread runner3 = new Thread(runnableObjectClient2);
//        Thread runner4 = new Thread(runnableObjectClient3);
        runner1.start();
        runner2.start();
        runner3.start();
        runner4.start();
        runner1.join();
        runner2.join();
        runner3.join();
        runner4.join();
    }
}
import java.io.IOException;

public class Application {
    public static void main(String [] args) throws InterruptedException, IOException {
        RunnableClassServer runnableObjectServer = new RunnableClassServer();
        RunnableClassClient runnableObjectClient = new RunnableClassClient();
        RunnableClassClient runnableObjectClient2 = new RunnableClassClient();
        RunnableClassClient runnableObjectClient3 = new RunnableClassClient();
        Thread runner1 = new Thread(runnableObjectServer);
        Thread runner2 = new Thread(runnableObjectClient);
        Thread runner3 = new Thread(runnableObjectClient2);
        Thread runner4 = new Thread(runnableObjectClient3);
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
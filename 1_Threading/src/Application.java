import java.io.IOException;

public class Application {
    public static void main(String [] args) throws InterruptedException, IOException {
        RunnableClassClient runnableObjectClient = new RunnableClassClient();
        RunnableClassServer runnableObjectServer = new RunnableClassServer();
        Thread runner1 = new Thread(runnableObjectClient);
        Thread runner2 = new Thread(runnableObjectServer);
        runner1.start();
        runner2.start();
        runner1.join();
        runner2.join();
    }
}
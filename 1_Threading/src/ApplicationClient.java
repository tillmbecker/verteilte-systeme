public class ApplicationClient {
    public static void main(String[] args) {
        RunnableClassClient runnableClassClient = new RunnableClassClient(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Thread thread = new Thread(runnableClassClient);
        thread.start();
    }
}

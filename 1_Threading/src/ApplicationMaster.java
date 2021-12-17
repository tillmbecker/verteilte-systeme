public class ApplicationMaster {
    public static void main(String[] args) {
        RunnableClassMaster runnableClassMaster = new RunnableClassMaster(Integer.parseInt(args[0]));
        Thread thread = new Thread(runnableClassMaster);
        thread.start();
    }
}

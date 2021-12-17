public class ApplicationSlave {
    public static void main(String[] args) {
        int amoutOfSlaves = Integer.parseInt(args[0]);

        for (int i = 0; i < amoutOfSlaves; i++) {
            RunnableClassSlave runnableClassSlave = new RunnableClassSlave(Integer.parseInt(args[0]), Integer.parseInt(args[1]) + i, args[2], Integer.parseInt(args[3]));
            Thread thread = new Thread(runnableClassSlave);
            thread.start();
        }

    }
}

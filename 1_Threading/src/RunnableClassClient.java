import java.io.IOException;

public class RunnableClassClient implements Runnable {
    private Client client;
    private int amountOfPrimes;

    public RunnableClassClient(String host, int port, int amountOfPrimes) {
        client = new Client(host, port, amountOfPrimes);
        this.amountOfPrimes = amountOfPrimes;
    }

    public void run() {
        try {
            client.connect();
            client.createRSA(amountOfPrimes);
            client.disconnect();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
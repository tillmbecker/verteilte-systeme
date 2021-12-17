import java.io.IOException;

public class RunnableClassClient implements Runnable {
    private Client client;
    private int amountOfPrimes;

    public RunnableClassClient(String host, int port, int amountOfPrimes) {
        client = new Client(host, port);
        this.amountOfPrimes = amountOfPrimes;
    }

    public void run() {
        try {
            client.connect();
            client.sendMessages();
            client.createRSA(amountOfPrimes);

//            client.requestLastMessage();
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class Master {
    private int port;
    Boolean connectionOpen;
    private ServerSocket server;

    private ConcurrentHashMap<Integer, Node> connectionMap;
    private List<RequestHandler> requestHandlerList;

    public Master(int port) {
        this.port = port;
        connectionOpen = false;
        connectionMap = new ConcurrentHashMap<Integer, Node>();
        requestHandlerList = new ArrayList<RequestHandler>();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Master master = new Master(9876);
        master.start();
        master.delegateConnections();
    }

    public void start() throws IOException {
        // Create the socket server object
        this.server = new ServerSocket(port);
//        System.out.println("Some address " + server.getLocalSocketAddress());
        // Open the connection
        connectionOpen = true;
    }

    public void delegateConnections() throws IOException {
        while (connectionOpen) {
            Socket socket = null;

            try {
                socket = server.accept();
                // Confirm client connection
                System.out.println("New Slave connected: " + socket);

                // Create new object streams for the created socket
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                // Create ClientHandler thread and start it
                RequestHandler requestHandler = new RequestHandler(this, socket, objectInputStream, objectOutputStream, connectionMap);
                requestHandler.start();
                requestHandlerList.add(requestHandler);
            } catch (Exception e) {
                socket.close();
                e.printStackTrace();
            }
        }
    }

    public void sendRSASuccessMessage(Message message) {
        for (RequestHandler requestHandler: requestHandlerList) {
            requestHandler.sendRSASuccessMessage(message);
        }
    }
    public void delegateRSA(int amountOfPrimes, String chiffre, String publicKey) {
        FileEditor fileEditor = new FileEditor();
        File file = null;

        switch (amountOfPrimes) {
            case 100:
                file = new File("primes/100.txt");
                break;
            case 1000:
                file = new File("primes/1000.txt");
                break;
            case 10000:
                file = new File("primes/10000.txt");
                break;
            case 100000:
                file = new File("primes/100000.txt");
                break;
            default:
                System.out.println("Amount of primes not set correctly");
                break;
        }
        // Get primes list from file
        ArrayList<String> primesList = fileEditor.readFile(file);

        // Get amount of primes
        double fileContentsSize = primesList.size();
        // Get amount of slaves
        double numberOfSlaves = requestHandlerList.size();

        // Calculate partition size
        double partitionSizeDouble = (fileContentsSize / numberOfSlaves);
        partitionSizeDouble = Math.ceil(partitionSizeDouble);
        int partitionSize = (int) partitionSizeDouble;

        int alternateIndex = partitionSize;
        partitionSize++;

        // New list stores RSAPayloads
        ArrayList<RSAPayload> rsaPayloads = new ArrayList<>();

        // Iterate over primesList
        for (int i = 0; i < primesList.size(); i += partitionSize) {
            // Fill RSAPayloads with content and their respective borders
            if (alternateIndex < primesList.size()) {
                rsaPayloads.add(new RSAPayload(chiffre, publicKey, i, alternateIndex, primesList));
            } else {
                rsaPayloads.add(new RSAPayload(chiffre, publicKey, i, primesList.size(), primesList));
            }
            alternateIndex += partitionSize;
        }

        // Pass list elements to all Request Handlers
        int index = 0;
        for (RequestHandler handler : requestHandlerList) {
            handler.sendRSARequest(rsaPayloads.get(index));
            index++;
        }
    }

    public void stopRSA() {
        for (RequestHandler requestHandler: requestHandlerList) {
            requestHandler.stopRSA();
        }
    }
}

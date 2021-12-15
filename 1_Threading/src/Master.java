import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.Instant;

public class Master {
    private int port;
    Boolean connectionOpen;
    private ServerSocket server;

    private Map<Integer,Node> connectionMap;

    public Master (int port) {
        this.port = port;
        connectionOpen = false;
//        FIXME: ConcurrentHashMap scheint hierfür besser geeignet zu sein
        connectionMap = Collections.synchronizedMap(new HashMap<Integer,Node>());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Master master = new Master(9876);
        master.start();
        master.delegateConnections();
    }

    public void start() throws IOException {
        // Create the socket server object
        this.server = new ServerSocket(port);
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
                Thread thread = new RequestHandler(this, socket, objectInputStream, objectOutputStream, connectionMap);
                thread.start();
            } catch (Exception e) {
                socket.close();
                e.printStackTrace();
            }
        }
    }

    public void delegateRSA(int amountOfPrimes, String chiffre, String publicKey) {
        System.out.println("delegating");

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
        List<String> fileContents = fileEditor.readFile(file);

        int numberOfSlaves = connectionMap.size();
    }
}

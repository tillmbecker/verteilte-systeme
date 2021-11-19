import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Slave {

    private int port;
    Boolean connectionOpen;
    private ServerSocket server;
    private String masterhost;
    private int masterport;
    private Socket mastersocket;
    ObjectInputStream masterobjectInputStream;
    ObjectOutputStream masterobjectOutputStream;

    public Slave(int port, String masterhost, int masterport) {
        this.port = port;
        this.masterhost = masterhost;
        this.masterport = masterport;
        connectionOpen = false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Slave slave = new Slave(9006, "localhost", 9876);
        slave.connect();
        slave.connecttoMaster();
        slave.delegateConnections();
    }

    public void connect() throws IOException {
        // Create the socket server object
        this.server = new ServerSocket(port);
        // Open the connection
        connectionOpen = true;
    }

    public void connecttoMaster() throws IOException {
        this.mastersocket = new Socket(masterhost, masterport);
        masterobjectInputStream = new ObjectInputStream(mastersocket.getInputStream());
        masterobjectOutputStream = new ObjectOutputStream(mastersocket.getOutputStream());
    }

    public void delegateConnections() throws IOException {
        System.out.println("landed here");
        while (connectionOpen) {
            Socket socket = null;

            try {
                socket = server.accept();

                // Confirm client connection
                System.out.println("New Client connected: " + socket);

                System.out.println("stillworking0");

                // Create new object streams for the created socket

                System.out.println("fist stream worked");

                System.out.println("still working1");
                ObjectInputStream clientobjectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream clientobjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("still working2");
                // Create ClientHandler thread and start it
                Thread thread = new ClientHandler(socket, masterobjectInputStream, masterobjectOutputStream, clientobjectInputStream, clientobjectOutputStream);
                System.out.println("still 3");
                thread.start();
                System.out.println("still 4");

            } catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Server {

    private int port;
    Boolean connectionOpen;
    private ServerSocket server;

    public Server(int port) {
        this.port = port;
        connectionOpen = false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Server server = new Server(9876);
        server.connect();
        server.delegateConnections();
    }

    public void connect() throws IOException {
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
                System.out.println("New Client connected: " + socket);

                // Create new object streams for the created socket
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                // Create ClientHandler thread and start it
                Thread thread = new RequestHandler(socket, objectInputStream, objectOutputStream);
                thread.start();

            } catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Master {

    private int port;
    Boolean connectionOpen;
    private ServerSocket server;

    private Map<Integer,Node> connectionMap = null;

    public Master (int port) {
        this.port = port;
        connectionOpen = false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Master master = new Master(9876);
        //Master master2 = new Master(9877);
        master.start();
        //master2.start();
        master.delegateConnections();
        //master2.delegateConnections();
    }

    public void start() throws IOException {
        // Create the socket server object
        this.server = new ServerSocket(port);
        // Open the connection
        connectionOpen = true;
    }

    public void delegateConnections() throws IOException {
        connectionMap = Collections.synchronizedMap(new HashMap<Integer,Node>());

        while (connectionOpen) {
            Socket socket = null;

            try {
                socket = server.accept();
                // Confirm client connection
                System.out.println("New Slave connected: " + socket);

                // Create new object streams for the created socket
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("---vor-->"+connectionMap);
                // Create ClientHandler thread and start it
                Thread thread = new RequestHandler(socket, objectInputStream, objectOutputStream,connectionMap);
                thread.start();

                System.out.println("---danach-->"+connectionMap);

            } catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}

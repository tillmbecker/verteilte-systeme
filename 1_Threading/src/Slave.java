import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;

public class Slave {

    // Variable declaration for master
    private ServerSocket server;
    private int masterPort;
    private String masterHost;
    private ObjectOutputStream masterObjectOutputStream;
    private ObjectInputStream masterObjectInputStream;

    //  Variable declaration for slave
    private int slavePort;
    private Socket socket;
    private String messageSender;

    // Variable declaration for client
    private Boolean clientConnectionOpen;
    private ObjectInputStream clientObjectInputStream;
    private ObjectOutputStream clientObjectOutputStream;


    public Slave(int slavePort, String masterHost, int masterPort) {
        this.slavePort = slavePort;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        clientConnectionOpen = false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Slave slave = new Slave(9999, "localhost", 9876);
        slave.start();
        slave.connectToMaster();
        slave.delegateConnections();
    }

    public void start() throws IOException {
        // Create the socket server object
        this.server = new ServerSocket(slavePort);
        // Open the client connection
        clientConnectionOpen = true;
    }

    public void connectToMaster() throws IOException {
        // Connect to server
        this.socket = new Socket(masterHost, masterPort);
        //write to socket using ObjectOutputStream
        masterObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        masterObjectInputStream = new ObjectInputStream(socket.getInputStream());

        Message connectMessage = new Message();
        connectMessage.setType("connect");
        connectMessage.setSender("Slave " + socket);
        connectMessage.setReceiver("Master " + server);
        connectMessage.setSequenceNo(1);
        connectMessage.setPayload(socket.getPort());

        messageSender = "Slave, " + socket.getLocalPort();
    }

    public void delegateConnections() throws IOException {
        Message clientMessage;
        Message masterMessage;

        while (clientConnectionOpen) {
            Socket socket = null;

            try {
                socket = server.accept();

                // Confirm client connection
                System.out.println("New Client connected: " + socket);
            } catch (Exception e){
                socket.close();
                e.printStackTrace();
            }

            try {
                // Read messages from client and send to master
                clientMessage = (Message) clientObjectInputStream.readObject();
                masterObjectOutputStream.writeObject(clientMessage);
                masterObjectOutputStream.flush();

                // Read messages from master and send to client
                masterMessage = (Message) masterObjectInputStream.readObject();
                clientObjectOutputStream.writeObject(masterMessage);
                clientObjectOutputStream.flush();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(messageSender + ": Disconnecting " + socket);
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                System.out.println(messageSender + ": Client disconnected.");
                clientConnectionOpen = false;
                break;
            }
        }
        // Close resources
        try {
            clientObjectInputStream.close();
            clientObjectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
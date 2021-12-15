import java.io.EOFException;
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

    // Variable declaration for slave
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
        slave.waitForClientConnection();
//        slave.delegateConnections();
    }

    public void start() throws IOException {
        // Create the socket server object
        this.server = new ServerSocket(slavePort);
        // Open the client connection
    }

    public void connectToMaster() throws IOException, ClassNotFoundException {
        // Connect to server
        this.socket = new Socket(masterHost, masterPort);
        //write to socket using ObjectOutputStream
        masterObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        masterObjectInputStream = new ObjectInputStream(socket.getInputStream());
        messageSender = "Slave, " + socket.getLocalPort();

        sendJoinMessage();
    }

    public void sendJoinMessage() throws IOException, ClassNotFoundException {
//      FIXME: Message-Sende-Protokoll-Methoden in eine andere Klasse auslagern
        Message outgoingMessage = new Message();
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setPayload(String.valueOf(socket.getLocalPort()));
        outgoingMessage.setType("join");
        outgoingMessage.setSequenceNo(0);

        masterObjectOutputStream.writeObject(outgoingMessage);
        masterObjectOutputStream.flush();

        Message incomingMessage = (Message) masterObjectInputStream.readObject();
        printMasterMessages(String.valueOf(incomingMessage.getPayload()), incomingMessage.getSequenceNo(), incomingMessage.getType());
    }

    public void printMasterMessages(String payload, int sequenceNumber, String type) {
        System.out.println("---\n" + messageSender + " - Message received: " + "\n*Payload:\n" + payload +  "\n*Sequence Number: " + sequenceNumber + "\n*Type: " + type + "\n---");
    }

    public void delegateConnections() throws IOException {
        Message clientMessage;
        Message masterMessage;

        while (clientConnectionOpen) {
            try {
                // Read messages from client and send to master
                clientMessage = (Message) clientObjectInputStream.readObject();
                masterObjectOutputStream.writeObject(clientMessage);
                masterObjectOutputStream.flush();

                System.out.println(messageSender + " - Message received: " + clientMessage.getPayload());

                // Read messages from master and send to client
                masterMessage = (Message) masterObjectInputStream.readObject();
                clientObjectOutputStream.writeObject(masterMessage);
                clientObjectOutputStream.flush();
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println(messageSender + ": Waiting for client message");
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            catch (EOFException e) {
                System.out.println(messageSender + ": Disconnecting " + socket);
                disconnectClientSlaveConnection();

                System.out.println(messageSender + ": Client disconnected.");

                // After client disconnects, slave is made available for a new client
                clientConnectionOpen = false;

                // Wait for new client connection
                waitForClientConnection();
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

    public void waitForClientConnection() throws IOException {
        Socket socket = new Socket();

        while (clientConnectionOpen == false) {
            try {
                socket = server.accept();

                clientObjectInputStream  = new ObjectInputStream(socket.getInputStream());
                clientObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                // Confirm client connection
                System.out.println("New Client connected: " + socket);
                clientConnectionOpen = true;
            } catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }

        delegateConnections();
    }

    public void disconnectClientSlaveConnection() throws IOException {
        clientObjectOutputStream.close();
        clientObjectInputStream.close();
    }
}
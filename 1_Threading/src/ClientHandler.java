import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends Thread {
    private Slave slave;
    private int slavePort;
    private ServerSocket server;
    private Socket clientSocket;
    private ObjectInputStream clientObjectInputStream;
    private ObjectOutputStream clientObjectOutputStream;
    private ObjectInputStream masterObjectInputStream;
    private ObjectOutputStream masterObjectOutputStream;

    private boolean clientConnectionOpen;
    private String messageSender;

    public ClientHandler(Slave slave, ObjectInputStream masterObjectInputStream, ObjectOutputStream masterObjectOutputStream, int slavePort) throws IOException {
        this.slave = slave;
        this.masterObjectInputStream = masterObjectInputStream;
        this.masterObjectOutputStream = masterObjectOutputStream;
        this.slavePort = slavePort;
        this.server = new ServerSocket(slavePort);
        messageSender = "Slave, " + server.getLocalPort();
    }

    @Override
    public void run() {
        try {
            waitForClientConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitForClientConnection() throws IOException {
        clientSocket = new Socket();
        System.out.println(messageSender + ": Waiting for client connection");
        while (clientConnectionOpen == false) {
            try {
                clientSocket = server.accept();
                clientObjectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                clientObjectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                // Confirm client connection
                System.out.println("New Client connected: " + clientSocket);
                clientConnectionOpen = true;
            } catch (Exception e) {
                clientSocket.close();
                e.printStackTrace();
            }
        }

        delegateConnections();
    }

    public void delegateConnections() throws IOException {
        Message clientMessage;
        Message masterMessage;

        while (clientConnectionOpen) {
            try {
                // Read messages from client and send to master
                clientMessage = (Message) clientObjectInputStream.readObject();
                slave.forwardToMaster(clientMessage);

            } catch (SocketException e) {
                System.out.println(messageSender + ": Client disconnected unexpectedly - " + clientSocket);
                clientConnectionOpen = false;
                waitForClientConnection();
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println(messageSender + ": Waiting for client message");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                System.out.println(messageSender + ": Disconnecting " + clientSocket);
                disconnectClientSlaveConnection();

                System.out.println(messageSender + ": Client disconnected.");

                // After client disconnects, slave is made available for a new client
                clientConnectionOpen = false;

                // Wait for new client connection
                waitForClientConnection();
                e.printStackTrace();
            }
        }
        // Close resources
        disconnectClientSlaveConnection();
    }

    public void forwardToClient(Message message) throws IOException, ClassNotFoundException {
        // Read messages from master and send to client
//        System.out.println(messageSender + " - Message received: " + message.getPayload());

        clientObjectOutputStream.writeObject(message);
        clientObjectOutputStream.flush();
    }

    public void disconnectClientSlaveConnection() throws IOException {
        clientObjectOutputStream.close();
        clientObjectInputStream.close();
    }

}
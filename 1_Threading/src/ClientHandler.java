import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;

public class ClientHandler extends Thread {
    final private Socket socket;
    final private ObjectInputStream clientObjectInputStream;
    final private ObjectOutputStream clientObjectOutputStream;
    final private ObjectInputStream masterObjectInputStream;
    final private ObjectOutputStream masterObjectOutputStream;

    private boolean connectionOpen;
    private String messageSender;

    public ClientHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, ObjectInputStream masterObjectInputStream, ObjectOutputStream masterObjectOutputStream) {
        this.socket = socket;
        this.clientObjectInputStream = objectInputStream;
        this.clientObjectOutputStream = objectOutputStream;
        this.masterObjectInputStream = masterObjectInputStream;
        this.masterObjectOutputStream = masterObjectOutputStream;
        connectionOpen = true;
        messageSender = "Slave, " + socket.getLocalPort();
    }

    @Override
    public void run() {
        Message clientMessage;
        Message masterMessage;

        while (connectionOpen) {
//            System.out.println(messageSender + ": Waiting for client request");

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
                connectionOpen = false;
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
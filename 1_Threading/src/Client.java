import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class implements java socket client
 * @author pankaj
 *
 */
public class Client {

    private String host;
    private int port;
    private Socket socket;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        // Connect to server
        this.socket = new Socket(host, port);
    }

    public void sendMessages() throws ClassNotFoundException, IOException {
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;

        //write to socket using ObjectOutputStream
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        String incomingMessage;

        for (int i=0; i<5;i++) {

            System.out.println("Client: Sending request to Socket Server");

            Message messageSent = new Message("Some text " + i);

            objectOutputStream.writeObject(messageSent);
            objectOutputStream.flush();

            incomingMessage = (String) objectInputStream.readObject();
            System.out.println("Client: Message Received: " + incomingMessage);
        }

        Message exitMessage = new Message("exit");
        objectOutputStream.writeObject(exitMessage);
        objectOutputStream.flush();

        //close resources
        objectInputStream.close();
        objectOutputStream.close();
    }

    public void requestLastMessage() throws IOException {
        ObjectOutputStream objectOutputStream;

        Message message = new Message("!/lastmessage/!");
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);

        ObjectInputStream objectInputStream;
    }
}
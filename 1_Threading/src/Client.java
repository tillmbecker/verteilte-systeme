import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;

/**
 * This class implements java socket client
 * @author tillmbecker
 *
 */
public class Client {

    private String host;
    private int port;
    private Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        // Connect to server
        this.socket = new Socket(host, port);
        //write to socket using ObjectOutputStream
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessages() throws ClassNotFoundException, IOException {
        Message incomingMessage;
        Message outgoingMessage = new Message();

        for (int i=0; i<5;i++) {

            System.out.println("Client: Sending request to Socket Server");
            // Outgoing message text
            String messageText = "Some text " + i;
            // Fill outgoingMessage with content
            outgoingMessage.setReceiver("Server");
            outgoingMessage.setSender("Client");
            outgoingMessage.setTime(Instant.now());
            outgoingMessage.setPayload(messageText);


            objectOutputStream.writeObject(outgoingMessage);
            objectOutputStream.flush();

            incomingMessage = (Message) objectInputStream.readObject();
            System.out.println("Client - Message Received: " + incomingMessage.getPayload());
        }

        requestLastMessage();

        closeServer();

//        ToDo: Die Streams schließen bringt das Programm zum Absturz, obwohl der Server schon geschlossen wurde
        //close resources
//        objectInputStream.close();
//        objectOutputStream.close();
    }

    public void requestLastMessage() throws IOException, ClassNotFoundException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "!/lastmessage/!";
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender("Client");
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();

        Message lastMessage = (Message) objectInputStream.readObject();

        System.out.println("Client - Last message to server: "+ lastMessage.getPayload());
    }

    public void closeServer() throws IOException {

        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "!/exit/!";
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender("Client");
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }
}
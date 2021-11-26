import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
    private String messageSender;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Client client = new Client("localhost", 9999);
        client.connect();
//        TimeUnit.SECONDS.sleep(5);

        client.sendMessages();
    }

    public void connect() throws IOException {
        // Connect to server
        this.socket = new Socket(host, port);
        //write to socket using ObjectOutputStream
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.messageSender = "Client, " + socket.getLocalPort();
    }

    public void sendMessages() throws ClassNotFoundException, IOException, InterruptedException {
        Message incomingMessage;

        for (int i=0; i<5;i++) {
            System.out.println(messageSender + ": Sending request to Socket Server");
            // Outgoing message text
            String messageText = "" + i;
            // Fill outgoingMessage with content
            Message outgoingMessage = new Message();
            outgoingMessage.setReceiver("Server");
            outgoingMessage.setSender(messageSender);
            outgoingMessage.setPayload(messageText);

            objectOutputStream.writeObject(outgoingMessage);
            objectOutputStream.flush();

            // Read incoming messages
            incomingMessage = (Message) objectInputStream.readObject();
            System.out.println(messageSender + " - Message Received: " + incomingMessage.getPayload());
        }

        TimeUnit.SECONDS.sleep(5);

//        requestLastMessage();
        disconnect();
//        closeServer();

//        ToDo: Die Streams schließen bringt das Programm zum Absturz, obwohl der Server schon geschlossen wurde
    }

    public void requestLastMessage() throws IOException, ClassNotFoundException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "!/lastmessage/!";
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();

        Message lastMessage = (Message) objectInputStream.readObject();

        System.out.println(messageSender + " - Last message to server: "+ lastMessage.getPayload());
    }

    public void closeServer() throws IOException {

        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "!/exit/!";
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }

    public void disconnect() throws IOException {
        //close resources
//        objectOutputStream
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }
}
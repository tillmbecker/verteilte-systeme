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
    private ServerSocket server;
    Socket socket;
    Boolean connectionOpen;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    public Server(int port) {
        this.port = port;
        connectionOpen = false;
    }

    public void connect() throws IOException {
        //create the socket server object
        this.server = new ServerSocket(port);
        //creating socket and waiting for client connection
        this.socket = server.accept();
        connectionOpen = true;

        //read from socket to ObjectInputStream object
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        //create ObjectOutputStream object
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public void receiveMessages() throws IOException, ClassNotFoundException {
        Message incomingMessage;

        while (connectionOpen) {
            System.out.println("Server: Waiting for client request");

            //convert ObjectInputStream object to Message
            incomingMessage = (Message) objectInputStream.readObject();
            String incomingMessagePayload = (String) incomingMessage.getPayload();

            System.out.println("Server - Message received: " + incomingMessagePayload);

            sendMessageConfirmation(incomingMessagePayload);

            if(incomingMessagePayload.contains("!/lastmessage/!")) sendLastMessage();

            //terminate the server if client sends exit request
            if(incomingMessagePayload.contains(("!/exit/!"))) connectionOpen = false;

            // Save message from client in message_store.txt
            messageStore(incomingMessagePayload + " | " + incomingMessage.getTime());
        }
        disconnect();

        //close resources
        objectInputStream.close();
        objectOutputStream.close();
    }

    public void sendMessageConfirmation (String text) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Message received: " + text;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender("Server");
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
    }

    public void disconnect() throws IOException {
        System.out.println("Shutting down Socket server!");
        //close the ServerSocket object
        socket.close();
        server.close();
    }

    public void messageStore(String message) {
        FileEditor fileEditor = new FileEditor();
        File messageFile = fileEditor.createFile("message_store.txt");
        fileEditor.writeFile(messageFile, message);
    }

    public void sendLastMessage() throws IOException {
        FileEditor fileEditor = new FileEditor();
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = fileEditor.readLastLine("message_store.txt");
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender("Server");
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }
}
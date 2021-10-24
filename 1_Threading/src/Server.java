import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;
    private ServerSocket server;
    Socket socket;

    public Server(int port) {
        this.port = port;

    }

    public void connect() throws IOException {
        //create the socket server object
        this.server = new ServerSocket(port);
        //creating socket and waiting for client connection
        this.socket = server.accept();
    }

    public void receiveMessages() throws IOException, ClassNotFoundException {
        //read from socket to ObjectInputStream object
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        //create ObjectOutputStream object
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        Message message;

        while (true) {
            System.out.println("Server: Waiting for the client request");

            //convert ObjectInputStream object to Message
            message = (Message) objectInputStream.readObject();

            messageStore(message.getText() + " | " + message.getTimestamp());

            System.out.println("Server: Message Received: " + message.getText());

            //write object to Socket
            objectOutputStream.writeObject("Hi Client " + message.getText());

            if(message.getText().contains("!/lastmessage/!")) sendLastMessage();

            //terminate the server if client sends exit request
            if(message.getText().contains(("exit"))) break;
        }
        //close resources
        objectInputStream.close();
        objectOutputStream.close();

        disconnect();
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

    public void sendLastMessage() {

    }
}
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Map;

public class RequestHandler extends Thread {
    final private Socket socket;
    final private ObjectInputStream objectInputStream;
    final private ObjectOutputStream objectOutputStream;
    private boolean connectionOpen;
    private String messageSender;
    private Map<Integer,Node> connectionMap;

    public RequestHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Map<Integer,Node> connectionMap) {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.connectionMap = connectionMap;
        connectionOpen = true;
        messageSender = "Master, " + socket.getLocalPort();
    }

    @Override
    public void run() {
        Message incomingMessage = null;

        while (connectionOpen) {
            System.out.println(messageSender + ": Waiting for client request");

            //convert ObjectInputStream object to Message
            try {
                incomingMessage = (Message) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            String incomingMessagePayload = (String) incomingMessage.getPayload();
            //port from client
            String incomingMessageSender = incomingMessage.getSender();
            String[] incomingMessageSenderArr = incomingMessageSender.split(" ");
            int portClient =Integer.parseInt(incomingMessageSenderArr[1]);

            if (incomingMessagePayload == null) incomingMessagePayload = "";

            System.out.println(messageSender + " - RH: " + incomingMessagePayload);

            // connnectionMap
            Node node = new Node(portClient, false, socket );
            connectionMap.put(portClient, node);
            System.out.println("connectionMap RH: "+ connectionMap) ;


            try {
                sendMessageConfirmation(incomingMessagePayload);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(incomingMessagePayload.contains("!/lastmessage/!")) {
                try {
                    sendLastMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //terminate the server if client sends exit request
            if(incomingMessagePayload.contains(("!/exit/!"))) connectionOpen = false;

            // Save message from client in message_store.txt
            messageStore(incomingMessagePayload + " | " + incomingMessage.getTime());
        }

        //close resources
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageConfirmation (String text) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Message received from RH to CH: " + text;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
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
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }
}
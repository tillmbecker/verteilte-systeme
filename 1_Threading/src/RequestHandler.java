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

            // Convert ObjectInputStream object to Message
            try {
                incomingMessage = (Message) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Work on message request
            String incomingMessageType = incomingMessage.getType();
            String incomingMessagePayload = (String) incomingMessage.getPayload();
            int incomingMessageSequenceNumber = incomingMessage.getSequenceNo();

            // Port from client
            String incomingMessageSender = incomingMessage.getSender();
            String[] incomingMessageSenderArr = incomingMessageSender.split(" ");
            int portClient = Integer.parseInt(incomingMessageSenderArr[1]);

            if (incomingMessagePayload == null) incomingMessagePayload = "";

            switch (incomingMessageType) {
                case "connect":
                    // ToDo: Wie kriegen alle anderen Master Threads von der Node Liste mit?
                    System.out.println(messageSender + " - RH: " + incomingMessagePayload);

                    // connnectionMap
                    Node node = new Node(portClient, false, socket);
                    System.out.println("Clientport: " + node.getPortClient());
                    connectionMap.put(portClient, node);
                    System.out.println("connectionMap RH: "+ connectionMap);

                    break;
                case "write":
                    // Save message from client in message_store.txt
                    messageStore(incomingMessagePayload + " | " + incomingMessage.getTime());

                    // Send message confirmation
                    try {
                        sendMessageConfirmation(incomingMessagePayload, incomingMessageSequenceNumber);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Print message from client
                    printClientMessage(incomingMessagePayload, incomingMessageSequenceNumber, incomingMessageType);
                    break;
                case "read":
                    try {
                        sendLastMessage();

                        // Print message from client
                        printClientMessage(incomingMessagePayload, incomingMessage.getSequenceNo(), incomingMessageType);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

            //terminate the server if client sends exit request
//            if(incomingMessagePayload.contains(("!/exit/!"))) connectionOpen = false;



            // TODO: Add failsave for Socket Exception when slave disconnects
        }

        //close resources
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printClientMessage(String payload, int sequenceNumber, String type) {
        System.out.println(messageSender + " - Message received. " + " \nPayload: " + payload +  "\nSequence Number: " + sequenceNumber + "\nType: " + type);
    }

    public void sendMessageConfirmation (String payload, int sequenceNumber) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Message saved. \nPayload: " + payload +  "\nSequence Number: " + sequenceNumber;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
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
        System.out.println(messageText);
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }
}
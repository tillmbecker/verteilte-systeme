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

            // Extract message meta information
            String incomingMessageType = incomingMessage.getType();
            String incomingMessagePayload = (String) incomingMessage.getPayload();
            if (incomingMessagePayload == null) incomingMessagePayload = "";
            int incomingMessageSequenceNumber = incomingMessage.getSequenceNo();

            // Port from client
            String incomingMessageSender = incomingMessage.getSender();
            String[] incomingMessageSenderArr = incomingMessageSender.split(" ");

            // Work on message request
            switch (incomingMessageType) {
                case "connect":
                    //FIXME: Awaits client connection and not slave as it should be
                    // ToDo: Wie kriegen alle anderen Master Threads von der Node Liste mit?
//                    System.out.println(messageSender + " - RH: " + incomingMessagePayload);

                    // connnectionMap
                    int slavePort = Integer.parseInt(incomingMessagePayload);

                    Node node = new Node(slavePort, false, socket);
//                    System.out.println("Clientport: " + node.getPortClient());
                    connectionMap.put(slavePort, node);
                    System.out.println("ConnectionMap Update: "+ connectionMap);

                    printClientMessage(incomingMessagePayload, incomingMessageSequenceNumber, incomingMessageType);

                    // Send a message confirmation
                    try {
                        sendConnectionConfirmation(incomingMessagePayload, incomingMessageSequenceNumber);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
            // TODO: Add failsave for Socket Exception when slave disconnects
        }

        //Close resources
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printClientMessage(String payload, int sequenceNumber, String type) {
        System.out.println("---\n" + messageSender + " - Message received. " + " \nPayload: " + payload +  "\nSequence Number: " + sequenceNumber + "\nType: " + type);
    }

    public void sendConnectionConfirmation (String payload, int sequenceNumber) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Connection acknowledged. \nPayload: " + payload +  "\nSequence Number: " + sequenceNumber;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);
        outgoingMessage.setType("acknowledge");

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
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
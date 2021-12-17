import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.*;

public class RequestHandler extends Thread {
    private Master master;
    final private Socket socket;
    final private ObjectInputStream objectInputStream;
    final private ObjectOutputStream objectOutputStream;
    private boolean connectionOpen;
    private String messageSender;
    private Map<Integer, Node> connectionMap;

    public RequestHandler(Master master, Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Map<Integer, Node> connectionMap) {
        this.master = master;
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
            System.out.println(messageSender + ": Waiting for request");

            // Convert ObjectInputStream object to Message
            try {
                incomingMessage = (Message) objectInputStream.readObject();
            } catch (SocketException e) {
                connectionOpen = false;
                System.out.println("Slave disconnected unexpectedly: " + socket);
                break;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Extract message meta information
            String incomingMessageType = incomingMessage.getType();
            Object incomingMessagePayload = incomingMessage.getPayload();
            if (incomingMessagePayload == null) incomingMessagePayload = "";
            int incomingMessageSequenceNumber = incomingMessage.getSequenceNo();

            // Port from client
            String incomingMessageSender = incomingMessage.getSender();
            String[] incomingMessageSenderArr = incomingMessageSender.split(" ");

            int nodeId = connectionMap.size();

            // Work on message request
            switch (incomingMessageType) {
                case "join":
                    // connnectionMap
                    Node node = new Node(nodeId, false, socket);
                    connectionMap.put(nodeId, node);

                    printIncomingMessage((String) incomingMessagePayload, incomingMessageSequenceNumber, incomingMessageType);
                    // Send a message confirmation
                    try {
                        sendConnectionConfirmation((String) incomingMessagePayload, incomingMessageSequenceNumber);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "write":
                    // Save message from client in message_store.txt
                    messageStore(incomingMessagePayload + " | " + incomingMessage.getTime());

                    // Send message confirmation
                    try {
                        sendMessageConfirmation((String) incomingMessagePayload, incomingMessageSequenceNumber);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Print message from client
                    printIncomingMessage((String) incomingMessagePayload, incomingMessageSequenceNumber, incomingMessageType);
                    break;
                case "read":
                    try {
                        sendLastMessage();

                        // Print message from client
                        printIncomingMessage((String) incomingMessagePayload, incomingMessage.getSequenceNo(), incomingMessageType);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "rsa":
                    List<String> list = new ArrayList<String>((Collection<String>) incomingMessagePayload);
                    master.delegateRSA(Integer.parseInt(list.get(0)), list.get(1), list.get(2));
                    break;
                case "rsa-success":
                    master.sendRSASuccessMessage(incomingMessage);
                    master.stopRSA();
                    break;
                case "leave":
                    int slavePort = Integer.parseInt((String) incomingMessagePayload);

                    connectionMap.remove(slavePort);
                    System.out.println("ConnectionMap Update: " + connectionMap);

                    printIncomingMessage((String) incomingMessagePayload, incomingMessageSequenceNumber, incomingMessageType);
                    connectionOpen = false;
                    break;
                default:
                    break;
            }
        }

        //Close resources
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRSARequest(RSAPayload rsaPayload) {
        Message rsaMessage = new Message();

        // Fill outgoingMessage with content
        rsaMessage.setReceiver("Slave");
        rsaMessage.setSender(messageSender);
        rsaMessage.setTime(Instant.now());
        rsaMessage.setPayload(rsaPayload);
        rsaMessage.setType("rsa-slave");

        try {
            objectOutputStream.writeObject(rsaMessage);
        } catch (SocketException e) {
            System.out.println("Socket exception");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRSASuccessMessage(Message message) {
        message.setSender(messageSender);
        message.setReceiver("Slave");

        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRSA() {
        Message rsaMessage = new Message();

        // Fill outgoingMessage with content
        rsaMessage.setReceiver("Slave");
        rsaMessage.setSender(messageSender);
        rsaMessage.setTime(Instant.now());
        rsaMessage.setPayload("");
        rsaMessage.setType("rsa-stop");
        try {
            objectOutputStream.writeObject(rsaMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printIncomingMessage(String payload, int sequenceNumber, String type) {
        System.out.println("---\n" + messageSender + " - Message received. " + " \nPayload: " + payload + "\nSequence Number: " + sequenceNumber + "\nType: " + type);
    }

    public void sendConnectionConfirmation(String payload, int sequenceNumber) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Connection acknowledged. \nPayload: " + payload + "\nSequence Number: " + sequenceNumber;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);
        outgoingMessage.setType("acknowledge");

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }

    public void sendMessageConfirmation(String payload, int sequenceNumber) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Message saved. \nPayload: " + payload + "\nSequence Number: " + sequenceNumber;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);
        outgoingMessage.setType("response-client");

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
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();
    }
}
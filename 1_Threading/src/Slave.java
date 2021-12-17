import eu.boxwork.dhbw.examhelpers.rsa.RSAHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.Socket;
import java.util.ArrayList;

public class Slave {
    // Variable declaration for master
    private int masterPort;
    private String masterHost;
    private ObjectOutputStream masterObjectOutputStream;
    private ObjectInputStream masterObjectInputStream;

    // Variable declaration for slave
    private int slavePort;
    protected Socket socket;
    private String messageSender;
    private Boolean masterConnectionOpen;

    private ClientHandler clientHandler;
    private volatile DecryptionHandler decryptionHandler;
//    protected volatile boolean stopRSA;

    public Slave(int slavePort, String masterHost, int masterPort) {
        this.slavePort = slavePort;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        masterConnectionOpen = false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Slave slave = new Slave(9999, "localhost", 9876);
        slave.connectToMaster();
        slave.createClientHandler();
        slave.delegateConnections();
    }


    public void connectToMaster() throws IOException, ClassNotFoundException {
        // Connect to server
        this.socket = new Socket(masterHost, masterPort);
        //write to socket using ObjectOutputStream
        masterObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        masterObjectInputStream = new ObjectInputStream(socket.getInputStream());
        messageSender = "Slave, " + socket.getLocalPort();

        sendJoinMessage();
        masterConnectionOpen = true;
    }

    public void sendJoinMessage() throws IOException, ClassNotFoundException {
//      TODO: Message-Sende-Protokoll-Methoden in eine andere Klasse auslagern
        Message outgoingMessage = new Message();
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setPayload(String.valueOf(socket.getLocalPort()));
        outgoingMessage.setType("join");
        outgoingMessage.setSequenceNo(0);

        masterObjectOutputStream.writeObject(outgoingMessage);
        masterObjectOutputStream.flush();

        Message incomingMessage = (Message) masterObjectInputStream.readObject();
        printIncomingMessages(String.valueOf(incomingMessage.getPayload()), incomingMessage.getSequenceNo(), incomingMessage.getType());
    }

    public void createClientHandler() throws IOException {
        clientHandler = new ClientHandler(this, masterObjectInputStream, masterObjectOutputStream, slavePort);
        clientHandler.start();
    }

    public void printIncomingMessages(String payload, int sequenceNumber, String type) {
        System.out.println("---\n" + messageSender + " - Message received: " + "\n*Payload:\n" + payload + "\n*Sequence Number: " + sequenceNumber + "\n*Type: " + type + "\n---");
    }

    public void delegateConnections() throws IOException {
        Message masterMessage;

        while (masterConnectionOpen) {
            // Read messages from master
            try {
                masterMessage = (Message) masterObjectInputStream.readObject();

                String masterMessageType = masterMessage.getType();
                switch (masterMessageType) {
                    case "rsa-slave":
                        createDecryptionHandler((RSAPayload) masterMessage.getPayload());
                        break;
                    case "rsa-stop":
                        System.out.println("Stop the thing");
                        stopRSA();
                        break;
                    case "response-client":
                        clientHandler.forwardToClient(masterMessage);
                        break;
                    default:
                        break;

                }
                if (masterMessage.getType().equals("rsa-slave")) {

                }

            } catch (ClassNotFoundException | InterruptedException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }
        // Close resources
        disconnectFromMaster();
    }

    public void stopRSA() throws InterruptedException {
        decryptionHandler.interrupt();
//        decryptionHandler.join();
        decryptionHandler.stopRSA = true;
//        decryptionHandler.setStopRSA(true);
    }

    public void forwardToMaster(Message message) throws IOException {
        masterObjectOutputStream.writeObject(message);
        masterObjectOutputStream.flush();
    }

    public void createDecryptionHandler(RSAPayload rsaPayload) throws IOException {
        System.out.println(messageSender + ": Decrypting RSA");
        decryptionHandler = new DecryptionHandler(this, rsaPayload, messageSender);
        decryptionHandler.start();
//        decryptionHandler.stopRSA = true;
    }

    // TODO: Send message after decryption success
    // FIXME: Thread doesn't stop if you tell it to
    public void disconnectFromMaster() throws IOException {
        Message outgoingMessage = new Message();
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setPayload(String.valueOf(socket.getLocalPort()));
        outgoingMessage.setType("leave");
        outgoingMessage.setSequenceNo(0);

        masterObjectOutputStream.writeObject(outgoingMessage);
        masterObjectOutputStream.flush();

        try {
            masterObjectInputStream.close();
            masterObjectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
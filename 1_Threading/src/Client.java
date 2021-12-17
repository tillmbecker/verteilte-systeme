import eu.boxwork.dhbw.examhelpers.rsa.RSAHelper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements java socket client
 *
 * @author tillmbecker
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

        client.createRSA(100);
        client.disconnect();
    }

    public void connect() throws IOException, ClassNotFoundException {
        // Connect to server
        this.socket = new Socket(host, port);
        //write to socket using ObjectOutputStream
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //read the server response message
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.messageSender = "Client, " + socket.getLocalPort();

//        sendConnectMessage();
    }

    public void printMasterMessages(String payload, int sequenceNumber, String type) {
        System.out.println("---\n" + messageSender + " - Message received: " + "\n*Payload:\n" + payload + "\n*Sequence Number: " + sequenceNumber + "\n*Type: " + type + "\n---");
    }

    public void sendMessages() throws ClassNotFoundException, IOException, InterruptedException {
        Message incomingMessage;

        for (int i = 0; i < 5; i++) {
            System.out.println(messageSender + ": Sending request to Socket Server");
            // Outgoing message text
            String messageText = "" + i;
            // Fill outgoingMessage with content
            Message outgoingMessage = new Message();
            outgoingMessage.setReceiver("Server");
            outgoingMessage.setSender(messageSender);
            outgoingMessage.setPayload(messageText);
            outgoingMessage.setType("write");
            outgoingMessage.setSequenceNo(i);

            objectOutputStream.writeObject(outgoingMessage);
            objectOutputStream.flush();

            System.out.println(messageSender + " | Messsage sent: " + outgoingMessage.getPayload());

            // Read incoming messages
            incomingMessage = (Message) objectInputStream.readObject();
            printMasterMessages(String.valueOf(incomingMessage.getPayload()), incomingMessage.getSequenceNo(), incomingMessage.getType());
        }


//        requestLastMessage();
//        TimeUnit.SECONDS.sleep(2);
//        TimeUnit.SECONDS.sleep(20);

//        disconnect();
//        closeServer();

//        ToDo: Die Streams schließen bringt das Programm zum Absturz, obwohl der Server schon geschlossen wurde
    }

    public void createRSA(int amountOfPrimes) throws IOException, ClassNotFoundException {
        String publicKey = "";
        String chiffre = "";

        switch (amountOfPrimes) {
            case 100:
                publicKey = "298874689697528581074572362022003292763";
                chiffre = "b4820013b07bf8513ee59a905039fb631203c8b38ca3d59b475b4e4e092d3979";
                break;
            case 1000:
                publicKey = "249488851623337787855631201847950907117";
                chiffre = "55708f0326a16870b299f913984922c7b5b37725ce0f6670d963adc0dc3451c8";
                break;
            case 10000:
                publicKey = "237023640130486964288372516117459992717";
                chiffre = "a9fc180908ad5f60556fa42b3f76e30f48bcddfad906f312b6ca429f25cebbd0";
                break;
            case 100000:
                publicKey = "174351747363332207690026372465051206619";
                chiffre = "80f7b3b84e8354b36386c6833fe5c113445ce74cd30a21236a5c70f5fdca7208";
                break;
            default:
                System.out.println("Amount of primes not set correctly");
                break;
        }


        Message outgoingMessage = new Message();

        // Outgoing message list
        List<String> messagePayload = new ArrayList<String>();
        messagePayload.add(Integer.toString(amountOfPrimes));
        messagePayload.add(chiffre);
        messagePayload.add(publicKey);

        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messagePayload);
        outgoingMessage.setType("rsa");

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();

        Message rsaMessage = (Message) objectInputStream.readObject();
        System.out.println(rsaMessage.getPayload());
    }

    public void requestLastMessage() throws IOException, ClassNotFoundException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "";
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Server");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);
        outgoingMessage.setType("read");

        objectOutputStream.writeObject(outgoingMessage);
        objectOutputStream.flush();

        Message lastMessage = (Message) objectInputStream.readObject();

        printMasterMessages(String.valueOf(lastMessage.getPayload()), lastMessage.getSequenceNo(), lastMessage.getType());
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
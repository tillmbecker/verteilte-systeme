import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;

public class ClientHandler extends Thread {
    final private Socket socket;
    final private ObjectInputStream clientObjectInputStream;
    final private ObjectOutputStream clientObjectOutputStream;
    final private ObjectInputStream masterObjectInputStream;
    final private ObjectOutputStream masterObjectOutputStream;

    private boolean connectionOpen;
    private String messageSender;

    public ClientHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, ObjectInputStream masterObjectInputStream, ObjectOutputStream masterObjectOutputStream) {
        this.socket = socket;
        this.clientObjectInputStream = objectInputStream;
        this.clientObjectOutputStream = objectOutputStream;
        this.masterObjectInputStream = masterObjectInputStream;
        this.masterObjectOutputStream = masterObjectOutputStream;
        connectionOpen = true;
        messageSender = "Slave, " + socket.getLocalPort();
    }

    @Override
    public void run() {
        Message clientMessage;
        Message masterMessage;

        while (connectionOpen) {
            System.out.println(messageSender + ": Waiting for client request");

            try {
                // Read messages from client and send to master
                clientMessage = (Message) clientObjectInputStream.readObject();
                masterObjectOutputStream.writeObject(clientMessage);
                masterObjectOutputStream.flush();

                // Read messages from master and send to client
                masterMessage = (Message) masterObjectInputStream.readObject();
                String masterMessagePayload = (String) masterMessage.getPayload();
                if (masterMessagePayload == null) masterMessagePayload = "";
                System.out.println(messageSender + " - CH: " + masterMessagePayload);
                clientObjectOutputStream.writeObject(masterMessage);
                clientObjectOutputStream.flush();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(messageSender + ": Disconnecting " + socket);
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                System.out.println(messageSender + ": Client disconnected.");
                connectionOpen = false;
                break;
            }

//            String incomingMessagePayload = (String) incomingMessage.getPayload();
//            if (incomingMessagePayload == null) incomingMessagePayload = "";
//
//            System.out.println(messageSender + " - Message received: " + incomingMessagePayload);
//
//            try {
//                sendMessageConfirmation(incomingMessagePayload);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if(incomingMessagePayload.contains("!/lastmessage/!")) {
//                try {
//                    sendLastMessage();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //terminate the server if client sends exit request
//            if(incomingMessagePayload.contains(("!/exit/!"))) connectionOpen = false;
//
//            // Save message from client in message_store.txt
//            messageStore(incomingMessagePayload + " | " + incomingMessage.getTime());
        }

        // Close resources
        try {
            clientObjectInputStream.close();
            clientObjectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void sendMessageConfirmation (String text) throws IOException {
        Message outgoingMessage = new Message();

        // Outgoing message text
        String messageText = "Message received: " + text;
        // Fill outgoingMessage with content
        outgoingMessage.setReceiver("Client");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setTime(Instant.now());
        outgoingMessage.setPayload(messageText);

        clientObjectOutputStream.writeObject(outgoingMessage);
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

        clientObjectOutputStream.writeObject(outgoingMessage);
        clientObjectOutputStream.flush();
    }
}
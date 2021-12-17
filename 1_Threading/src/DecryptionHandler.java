import eu.boxwork.dhbw.examhelpers.rsa.RSAHelper;

import java.io.IOException;
import java.util.ArrayList;

public class DecryptionHandler extends Thread {
    private Slave slave;
    protected volatile boolean stopRSA;

    public void setStopRSA(boolean stopRSA) {
        this.stopRSA = stopRSA;
    }

    public DecryptionHandler(Slave slave, RSAPayload rsaPayload, String messageSender) throws IOException {
        this.slave = slave;
        stopRSA = false;
        bruteForceRSA(rsaPayload, messageSender);
    }

    public void bruteForceRSA(RSAPayload rsaPayload, String messageSender) throws IOException {

        String publicKey = rsaPayload.getPublicKey();

        String chiffre = rsaPayload.getChiffre();
        ArrayList<String> primesList = rsaPayload.getPrimesList();
        RSAHelper helper = new RSAHelper();
        int startIndex = rsaPayload.getStartIndex();
        int endIndex = rsaPayload.getEndIndex();
        boolean decrypted = false;

        for (String p : primesList) {
            if (Thread.currentThread().isInterrupted()) break;
            if (!decrypted) {
                for (int i = startIndex; i < endIndex; i++) {
                    if (stopRSA) break;
                    String q = primesList.get(i);
                    if (helper.isValid(p, q, publicKey)) {
                        System.out.println(messageSender + ": P/Q fit to the public key: " + helper.isValid(p, q, publicKey));
                        String decryptedCypher = helper.decrypt(p, q, chiffre);
                        System.out.println(messageSender + ": Decrypted text is: " + decryptedCypher);

                        sendSuccessMessage(messageSender, decryptedCypher);
                        decrypted = true;
                    }
                }
            }
        }
        if (stopRSA) {
            System.out.println(messageSender + ": Decryption stopped. Cypher decrypted.");
        } else if (!decrypted) {
            System.out.println(messageSender + ": Decrypt failed");
        }
    }

    public void sendSuccessMessage(String messageSender, String decryptedCypher) throws IOException {
        Message outgoingMessage = new Message();
        outgoingMessage.setReceiver("Master");
        outgoingMessage.setSender(messageSender);
        outgoingMessage.setPayload(decryptedCypher);
        outgoingMessage.setType("rsa-success");
        outgoingMessage.setSequenceNo(0);

        slave.forwardToMaster(outgoingMessage);
    }
}

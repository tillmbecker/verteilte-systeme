import java.io.Serializable;
import java.util.ArrayList;

public class RSAPayload implements Serializable {
    private String chiffre;
    private String publicKey;
    private int startIndex;
    private int endIndex;
    private ArrayList<String> primesList;

    public RSAPayload(String chiffre, String publicKey, int startIndex, int endIndex, ArrayList<String> primesList) {
        this.chiffre = chiffre;
        this.publicKey = publicKey;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.primesList = primesList;
    }

    public ArrayList<String> getPrimesList() {
        return primesList;
    }

    public String getChiffre() {
        return chiffre;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setChiffre(String chiffre) {
        this.chiffre = chiffre;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setPrimesList(ArrayList<String> primesList) {
        this.primesList = primesList;
    }
}

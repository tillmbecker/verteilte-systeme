import java.io.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Scanner; // Import the Scanner class to read text files
import java.time.Instant; // Timestamps

public class FileEditor {

    public FileEditor() {}

        public File createFile(String fileTitle) {
        return new File(fileTitle);
    }

    public void writeFile(File file, String text) {
        try {
            FileWriter writer = new FileWriter(file, true);

            writer.write(text + "\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void readFile(File file) {
        try {
            Scanner myReader = new Scanner(file);
            System.out.println("-Output--------");
            while (myReader.hasNextLine()) {
                String text = myReader.nextLine();
                System.out.println(text);
            }
            System.out.println("---------------");
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String readLastLine (String fileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("message_store.txt"));
            String line = reader.readLine();
            String lastLine = "";
            while (line != null) {
                lastLine = line;
                // read next line
                line = reader.readLine();
            }
            reader.close();
            return lastLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Timestamp addTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
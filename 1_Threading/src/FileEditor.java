import java.io.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

    public List<String> readFile(File file) {
        List<String> fileContent = new ArrayList<String>();
        Scanner myReader = null;
        try {
            myReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (myReader.hasNextLine()) {
            String text = myReader.nextLine();
            fileContent.add(text);
//            System.out.println(text);
        }
            myReader.close();
            return fileContent;
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
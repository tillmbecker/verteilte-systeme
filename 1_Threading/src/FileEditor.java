import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Scanner; // Import the Scanner class to read text files
import java.time.Instant; // Timestamps

public class FileEditor {

    public FileEditor() {}

    public File createFile(String fileTitle) {
        try {
            File file = new File(fileTitle);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            return file;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    public void writeFile(File file, String text) {
        try {
            FileWriter writer = new FileWriter(file, true);

            writer.write(text + "\n");

            writer.close();
            System.out.println("Successfully wrote to the file.");
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

    public Timestamp addTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
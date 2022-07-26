
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
    public static String readString(String fileName) {
        ClassLoader classLoader = Utils.class.getClassLoader();

        File file = new File(classLoader.getResource(fileName).getFile());

        //File is found
        System.out.println("File Found : " + file.exists());

        //Read File Content
        String content = null;
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
}

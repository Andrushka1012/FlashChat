package FlashChatServer.actions;

import FlashChatServer.StringObject;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ActionGetImage implements Action {
    private String action;
    private String msgId;

    @Override
    public void execute(PrintWriter out) {
        File file = new File("C:\\projects\\FlashChatServer\\FlashChatImages", msgId + ".txt");
        if (file.exists()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(file, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                out.println("error");
            }
            String imgage = scanner.useDelimiter("\\A").next();
            StringObject stringObject = new StringObject(imgage);
            Gson gson = new Gson();
            String jsonString = gson.toJson(stringObject);
            out.println(jsonString);

            scanner.close();
        }
    }
}

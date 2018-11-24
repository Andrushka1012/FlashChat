package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.StringObject;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ActionLoadImage implements Action{
    private String action;
    private String userId;
    private String imageId;


    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){

            try {
                Statement st = connection.createStatement();
                String s = new String("SELECT img_id  from Images" +
                        " where sender_id  = \'" + userId + "\' or recipient_id = \'" + userId + "\'");
                System.out.println(s);
                ResultSet result = st.executeQuery(s);
                while (result.next()){
                    String id = result.getString("img_id");
                    if (id.equals(imageId)){
                        //File file = new File("FlashChatImages",imageId + ".txt");
                        File file = new File("C:\\projects\\flashChat\\FlashChatServer\\FlashChatImages",imageId + ".txt");
                        if (file.exists()){
                            Scanner scanner = new Scanner(file,"UTF-8");
                            String encodedString = scanner.useDelimiter("\\A").next();
                            scanner.close();

                            Gson gson = new Gson();
                            StringObject str = new StringObject(encodedString);
                            String json = gson.toJson(str);
                            out.println(json);
                        }
                        connection.close();
                        return;
                    }
                }
                out.println("error");
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("error");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                out.println("not found");
            }
        }else{
            System.out.println("no connect to db");
            out.println("error");
        }
    }
}

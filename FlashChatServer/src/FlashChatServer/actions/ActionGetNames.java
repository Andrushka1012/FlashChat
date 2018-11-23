package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ActionGetNames implements Action{
    private String action;
    private String userId;

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                String sql = "SELECT USER_ID,name,image_src from USERS" +
                        " WHERE USER_ID in (SELECT SENDER_ID from MESSAGES where RECIPIENT_ID = \'" + userId + "\' group by SENDER_ID )" +
                        " or USER_ID in (SELECT RECIPIENT_ID from MESSAGES where SENDER_ID = '" + userId + "\' group by RECIPIENT_ID )" +
                        " or USER_ID = \'" + userId + "\'" +
                        " GROUP BY USER_ID,NAME,image_src";

                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);
                List<NameItem> list = new ArrayList<>();
                while (result.next()){
                    String userId = result.getString("USER_ID");
                    String name = result.getString("name");
                    String src = result.getString("image_src");

                    if(src != null && src.equals("no_facebook_url")){
                        File file = new File("C:\\projects\\FlashChatServer\\FlashChatImages",userId + ".txt");
                        if (file.exists()){
                            Scanner scanner = new Scanner(file,"UTF-8");
                            src = scanner.useDelimiter("\\A").next();
                            scanner.close();
                        }

                    }

                    NameItem item = new NameItem(userId,name,src);
                    list.add(item);
                }
                Type type = new TypeToken<List<NameItem>>(){}.getType();
                Gson gson = new Gson();
                System.out.println("size:" + list.size());
                String json = gson.toJson(list,type);
                System.out.println(json);
                out.println(json);

                connection.close();
            } catch (SQLException | FileNotFoundException e) {
                e.printStackTrace();
                out.println("error");
            }

        } else{
            System.out.println("no connect to db");
            out.println("error");
        }


    }

    private class NameItem{
        private String userId;
        private String name;
        private String imageSrc;

        public NameItem(String userId, String name,String imageSrc) {
            this.userId = userId;
            this.name = name;
            this.imageSrc = imageSrc;
        }
    }
}

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

public class ActionGetAllMessages implements Action{
    private String action;
    private String userId;

    @Override
    public void execute(PrintWriter out) {

        java.sql.Connection connection = ConnectionToDb.connectToDB(userId);
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                String sql = "SELECT * from Messages" +
                        " where sender_id = \'" + userId + "\'" + " or recipient_id = \'" + userId + "\'" ;
                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);
                List<Message> list = new ArrayList<>();
                while (result.next()){
                    String msgId = result.getString("msg_id");
                    String text = result.getString("text");
                    String senderId = result.getString("sender_id");
                    String recipient_id = result.getString("recipient_id");
                    int read = result.getInt("read");
                    int type = result.getInt("type");

                    String stringDate = result.getString("msg_date");
                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                            .parse(stringDate);
                    if (type == 1) { //image type
                        File file = new File("C:\\projects\\FlashChatServer\\FlashChatImages",msgId + ".txt");
                        if (file.exists()){
                            Scanner scanner = new Scanner(file,"UTF-8");
                            text = scanner.useDelimiter("\\A").next();
                            scanner.close();
                        }
                    }

                    Message msg = new Message(msgId,text,senderId,recipient_id,date,read,type);
                    list.add(msg);
                }
                Type type = new TypeToken<List<Message>>(){}.getType();
                Gson gson = new Gson();
                System.out.println("size:" + list.size());
                String json = gson.toJson(list,type);
             //   System.out.println(json);
                out.println(json);

                st.executeQuery(
                        "UPDATE Messages" +
                                " SET read = " + String.valueOf(1) +
                                " WHERE recipient_id = \'" + userId +"\'"
                );

                ConnectionToDb.makeCommit(connection);
                connection.close();
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                out.println("error");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else{
            System.out.println("no connect to db");
            out.println("error");
        }

    }
}

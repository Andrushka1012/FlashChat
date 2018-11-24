package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.Person;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class ActionCheckNewMessages implements Action{
    private String action;
    private String userId;

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                String sql = "SELECT count(*) from MESSAGES" +
                        "  WHERE RECIPIENT_ID = \'" + userId + "\' and ISREAD = 0";
                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);

                if (result.next()){
                    int count = result.getInt(1);
                    sql = "SELECT us.USER_ID,us.NAME,mes.TEXT from USERS us LEFT JOIN  MESSAGES mes on us.USER_ID = mes.SENDER_ID" +
                            " WHERE mes.RECIPIENT_ID = \'" + userId + "\' and mes.ISREAD = 0";

                    System.out.println(sql);
                    ResultSet resultIno = connection.createStatement().executeQuery(sql);

                    if (resultIno.next()){
                        String senderId = resultIno.getString(1);
                        String senderName = resultIno.getString(2);
                        String text = resultIno.getString(3);

                        NotificationItem item = new NotificationItem(count,senderId,senderName,text);
                        Gson gson = new Gson();
                        String json = gson.toJson(item);
                        System.out.println("json check message:" + json);

                        out.println(json);

                    }else {
                        out.println("no new messages");
                    }

                }else{
                    out.println("no new messages");
                }

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("error");
            }

        } else{
            System.out.println("no connect to db");
            out.println("error");
        }
    }
}
    class NotificationItem {
        private int count;
        private String senderId;
        private String senderName;
        private String text;

        public NotificationItem(int count, String senderId, String senderName, String text) {
            this.count = count;
            this.senderId = senderId;
            this.senderName = senderName;
            this.text = text;
        }
    }
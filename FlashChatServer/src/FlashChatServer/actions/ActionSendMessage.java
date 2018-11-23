package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.FirebaseUtil;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;


public class ActionSendMessage implements Action {
    private String action;
    private String msgId;
    private String text;
    private String senderId;
    private String recipientId;
    private int type;
    private FirebaseUtil firebaseUtil = new FirebaseUtil();

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB(senderId);
        if (connection != null) {
            try {
                Statement st = connection.createStatement();
                ResultSet result = st.executeQuery(
                        "SELECT * from Messages" +
                                " where msg_id  = \'" + msgId + "\'"
                );

                if (!result.next()) {

                    String currentTime = LocalDateTime.now().toString();
                    String sql = "INSERT INTO Messages VALUES (" +
                            "\'" + msgId + "\'," +
                            "\'" + text + "\'," +
                            "\'" + senderId + "\'," +
                            "\'" + recipientId + "\'," +
                            String.valueOf(0) + ',' +
                            String.valueOf(type) + ',' +
                            "\'" + currentTime + "\'" + ")";
                    System.out.println(sql);
                    st.executeQuery(sql);
                    ConnectionToDb.makeCommit(connection);
                    out.println("success");
                } else out.println("error");
                connection.close();

                firebaseUtil.user(recipientId).send(senderId, type == 1 ? "Image" : text);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("no connect to db");
            out.println("error");
        }

    }
}


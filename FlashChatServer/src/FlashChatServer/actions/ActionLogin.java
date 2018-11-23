package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.FirebaseUtil;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class ActionLogin implements Action{

    private String action;
    private String userId;
    private String email;
    private String password;
    private String deviceToken;
    FirebaseUtil firebaseUtil = new FirebaseUtil();

    @Override
    public void execute(PrintWriter out) {
        System.out.println(email + "|" + password);

        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            try {
                Statement st = connection.createStatement();

                ResultSet result = st.executeQuery(
                        "SELECT * from users" +
                                " where email = \'" + email + "\' and password = \'" + password + "\'"
                );

                if (result.next()) {
                    userId = result.getString("user_id");
                    out.println(userId);
                }
                else {
                    out.println("incorrect");
                }

                connection.close();

                firebaseUtil.user(userId)
                        .token(deviceToken)
                        .register();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("no connect to db");
            out.println("incorrect");

        }
    }
}

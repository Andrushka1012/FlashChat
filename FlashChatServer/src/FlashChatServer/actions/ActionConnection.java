package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class ActionConnection implements Action{

    private String action;
    private String id;


    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");

        try {
            Statement st = connection.createStatement();
            String currentTime = LocalDateTime.now().toString();
            st.executeQuery(
                    "UPDATE users" +
                            " SET last_query = \'" + currentTime + "\'" +
                            " WHERE user_id = \'" + id + "\'");
            ConnectionToDb.makeCommit(connection);
            out.println("updated");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("error");
        }
    }
}

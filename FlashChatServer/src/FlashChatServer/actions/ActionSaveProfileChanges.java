package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class ActionSaveProfileChanges implements Action{
    private String action;
    private String id;
    private String name;
    private String birth;
    private String email;
    private String number;
    private String gender;

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB(id);
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                String currentTime = LocalDateTime.now().toString();
                String sql = new String("UPDATE users" +
                        " SET name = \'" + name + "\'," +
                        " birthday = \'" + birth + "\'," +
                        " email = \'" + email + "\'," +
                        " phone_number = \'" + number + "\'," +
                        " gender = \'" +gender+"\'," +
                        " last_query = \'" + currentTime + "\'" +
                        " WHERE user_id = \'" + id + "\'");
                System.out.println(sql);
                st.executeQuery(sql);

                ConnectionToDb.makeCommit(connection);
                out.println("success");
                ConnectionToDb.makeCommit(connection);
                connection.close();

            } catch (SQLException e) {
                out.println("error");
                e.printStackTrace();
            }

        }else{
            System.out.println("no connect to db");
            out.println("error");

        }
    }
}

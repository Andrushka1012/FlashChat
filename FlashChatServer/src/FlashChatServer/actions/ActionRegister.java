package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class ActionRegister implements Action{

    private String action;
    private String name;
    private String birth;
    private String email;
    private String number;
    private String password;
    private String gender;

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                ResultSet result = st.executeQuery(
                        "SELECT * from users" +
                                " where email = \'" + email + "\'"
                );

                if (!result.next()){
                    String id = UUID.randomUUID().toString();
                    String currentTime = LocalDateTime.now().toString();
                    st.executeQuery(
                            "INSERT INTO users VALUES (" +
                                    "\'" + id + "\'," +
                                    "\'" + name + "\'," +
                                    "\'" + password + "\'," +
                                    "\'" + email + "\'," +
                                    "\'" + birth + "\'," +
                                    "\'" + number + "\'," +
                                    "\'" + gender + "\'," +
                                    "\'" + currentTime + "\'," +
                                    "\'" + "no_facebook_url" + "\')"
                    );
                    ConnectionToDb.makeCommit(connection);
                    out.println(id);
                }else out.println("invalid");

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else{
            System.out.println("no connect to db");
            out.println("invalid");
        }

    }
}

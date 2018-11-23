package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class ActionLoginFromFacebook implements Action{
    private String action;
    private String id;
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String email;
    private String gender;
    private String photoUrl;

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                ResultSet result = st.executeQuery(
                        "SELECT * from users" +
                                " where user_id = \'" + id + "\'"
                );

                if (!result.next()){
                    String currentTime = LocalDateTime.now().toString();
                    st.executeQuery(
                            "INSERT INTO users VALUES (" +
                                    "\'" + id + "\'," +
                                    "\'" + name + "\'," +
                                    "\'" + "facebook" + "\'," +
                                    "\'" + "null" + "\'," +
                                    "\'" + birthDate + "\'," +
                                    "\'" + phoneNumber + "\'," +
                                    "\'" + gender + "\'," +
                                    "\'" + currentTime + "\'," +
                                    "\'" + photoUrl + "\')"
                    );
                    ConnectionToDb.makeCommit(connection);
                    out.println(id);
                }else {
                    out.println(id);
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


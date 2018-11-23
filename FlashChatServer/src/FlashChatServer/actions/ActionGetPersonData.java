package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.Person;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class ActionGetPersonData implements Action{

    private String action;
    private String userId;

    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                ResultSet result = st.executeQuery(
                        "SELECT * from users" +
                                " where user_id = \'" + userId + "\'"
                );

                if (result.next()){
                    String id = result.getString("user_id");
                    String name = result.getString("name");
                    String birthDate = result.getString("birthday");
                    String phoneNumber = result.getString("phone_number");
                    String email = result.getString("email");
                    String gender = result.getString("gender");
                    String photoUrl = result.getString("image_src");

                    Person person = new Person(id,name,birthDate,phoneNumber,email,gender,photoUrl);

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(person);
                    System.out.println(jsonString);
                    out.println(jsonString);
                }else{
                    out.println("error");
                }

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


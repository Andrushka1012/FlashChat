package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;
import FlashChatServer.Message;
import FlashChatServer.Person;
import FlashChatServer.SearchItem;
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

public class ActionGetOnlinePersonData implements Action{
    private String action;
    private String userId;
    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            List<Person> personList = new ArrayList<>();

            try {
                Statement st = connection.createStatement();
                String sql = "SELECT * from Users" +
                                " where user_id in " +
                                     "(SELECT SENDER_ID from MESSAGES" +
                                     " WHERE RECIPIENT_ID LIKE \'" + userId + "\'" +
                                     " GROUP BY SENDER_ID)";
                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);

                    while (result.next()){
                        String lastQuery = result.getString("last_query");

                        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                                .parse(lastQuery);

                        Date timeBefore = new Date(System.currentTimeMillis() - (15*60*1000));
                        boolean online = date.after(timeBefore);

                        if (online) {
                            String senderId = result.getString("user_id");
                            String name = result.getString("name");
                            String birthDate = result.getString("birthday");
                            String phoneNumber = result.getString("phone_number");
                            String email = result.getString("email");
                            String gender = result.getString("gender");
                            String photoUrl = result.getString("image_src");

                            Person person = new Person(senderId, name, birthDate, phoneNumber, email, gender, photoUrl);
                            personList.add(person);
                        }
                    }

                Type type = new TypeToken<List<Person>>(){}.getType();
                Gson gson = new Gson();
                String json = gson.toJson(personList,type);
                System.out.println(json);
                out.println(json);

                connection.close();
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                out.println("error");
            }

        } else{
            System.out.println("no connect to db");
            out.println("error");
        }
    }
}

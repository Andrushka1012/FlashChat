package FlashChatServer.actions;


import FlashChatServer.ConnectionToDb;
import FlashChatServer.SearchItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActionSearch implements Action{
    private String searchString;
    private String userId;
    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");

        if (connection != null){

            Statement st = null;
            try {
                st = connection.createStatement();
                String sql = "SELECT user_id, name, last_query, image_src from users" +
                        " where name like \'%" + searchString + "%\' and user_id not like \'" + userId + "\'";
                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);

                List<SearchItem> list = new ArrayList<>();

                while (result.next()){
                    String id = result.getString("user_id");
                    String name = result.getString("name");
                    String lastQuery = result.getString("last_query");
                    String imageSrc = result.getString("image_src");

                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                            .parse(lastQuery);

                    Date timeBefore = new Date(System.currentTimeMillis() - (15*60*1000));

                    boolean online = date.after(timeBefore);
                    list.add(new SearchItem(name,id,online,imageSrc));
                }
                Type type = new TypeToken<List<SearchItem>>(){}.getType();
                Gson gson = new Gson();

                String json = gson.toJson(list,type);
                System.out.println(json);
                out.println(json);



            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                System.out.println("no connect to db");
                out.println("error");
            }

        }else{
            System.out.println("no connect to db");
            out.println("error");
        }

    }
}

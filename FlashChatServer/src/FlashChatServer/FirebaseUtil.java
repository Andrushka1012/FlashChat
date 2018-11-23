package FlashChatServer;


import FlashChatServer.actions.FirebaseRequest;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class FirebaseUtil {
    public static Retrofit retrofit;
    private String userId;
    private String deviceToken;

    public FirebaseUtil token(String deviceToken) {
        this.deviceToken = deviceToken;
        return this;
    }

    public FirebaseUtil user(String userId) {
        this.userId = userId;
        return this;
    }

    public void register() {
        if (userId == null || deviceToken == null) {
            System.out.println("Please provide dataPlease provide data");
            return;
        }
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null) {
            try {
                Statement st = connection.createStatement();
                st.executeQuery(
                        "INSERT INTO Devices VALUES (" +
                                "\'" + userId + "\'," +
                                "\'" + deviceToken + "\'" + ")"
                );
                ConnectionToDb.makeCommit(connection);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("no connect to db");
        }
    }

    public void delete() {
        if (deviceToken == null) {
            System.out.println("token = null");
            return;
        }
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null) {
            try {
                Statement st = connection.createStatement();

                st.executeQuery(
                        "DELETE FROM Devices " +
                                " where device_token  = \'" + deviceToken + "\'");
                ConnectionToDb.makeCommit(connection);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("no connect to db");
        }
    }

    public void send(String senderId, String content) {
        if (userId == null) {
            System.out.println("token = null");
            return;
        }
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null) {
            try {
                Statement st = connection.createStatement();
                String sql = "SELECT * from Devices" +
                        " where (user_id  = \'" + userId + "\')";
                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);

                while (result.next()) {
                    String device = result.getString("device_token");
                    HashMap<String, String> data = new HashMap<>();
                    data.put("senderId", senderId);
                    data.put("content", content);

                    sendMessage(device, data);
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("no connect to db");
        }

    }

    private void sendMessage(String device, HashMap<String, String> data) {
        FirebaseRequest firebaseRequest = new FirebaseRequest(device, "Titile");

        FirebaseClient client = retrofit.create(FirebaseClient.class);
        Call call =  client.sendMessage(firebaseRequest, FirebaseClient.TOKEN);
        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

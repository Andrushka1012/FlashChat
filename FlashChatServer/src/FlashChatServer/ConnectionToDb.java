package FlashChatServer;

import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Locale;

public class ConnectionToDb {


    private static String user = "system";
    private static String password = "ujyhuheda";
    //private static String userAWS = "andrushka";
    //private static String passwordAWS = "ujyhuheda1012";
    //private static String DBUrl = "jdbc:oracle:thin:@oracledb.ctzv4jontqi0.us-east-2.rds.amazonaws.com:1521:oracleDB";


    @Nullable
    public static java.sql.Connection connectToDB(String userId) {
        java.sql.Connection connection;
        try {
            Locale.setDefault(Locale.ENGLISH);
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/flashchat?user=root&password=root"); //connection to localhost mariaDB
            //connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",user,password); //connection to localhost oracle
            //connection = DriverManager.getConnection(DBUrl,user,password); //connection to lockalhost
            System.out.println("Connected");

            if (userId.isEmpty()) return connection;

            Statement st = connection.createStatement();
            String currentTime = LocalDateTime.now().toString();
            System.out.println(currentTime);
            st.executeQuery(
                    "UPDATE users" +
                            " SET last_query = \'" + currentTime + "\'" +
                            " WHERE user_id = \'" + userId + "\'");
            ConnectionToDb.makeCommit(connection);

            return connection;
        } catch (Exception E) {
            System.out.println("Don't Connected");
            E.printStackTrace();
            return null;
        }

    }

    public static void makeCommit(java.sql.Connection connection){
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                st.executeQuery("COMMIT");
                System.out.println("Commit was executed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Commit was not executed.");

        }

    }

    public static void executeQueryToDB(java.sql.Connection connection,String sql){
        if (connection != null){
            try {
                Statement st = connection.createStatement();

                ResultSet result = st.executeQuery(sql);

                while (result.next()){
                    System.out.println("id:" + result.getString(1) + " Name:" + result.getString(2) + "email:" + result.getString(4));
                }
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Query was not executed.");

        }

    }


}

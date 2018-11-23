package FlashChatServer.actions;
import FlashChatServer.ConnectionToDb;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ActionChanePassword implements Action{
    private String action;
    private String id;
    private String oldPassword;
    private String newPassword;

    @Override
    public void execute(PrintWriter out){
        java.sql.Connection connection = ConnectionToDb.connectToDB(id);
        if (connection != null){
            try {
                Statement st = connection.createStatement();
                String sql = "SELECT password from Users" +
                        " where user_id = \'" + id + "\'" ;
                System.out.println(sql);
                ResultSet result = st.executeQuery(sql);

                if (result.next()){
                    if (result.getString("password").equals(oldPassword)){
                        st.executeQuery(
                                "UPDATE Users" +
                                        " SET password = \'" + newPassword +"\'" +
                                        " WHERE user_id = \'" + id +"\'"
                        );
                        out.println("");
                        ConnectionToDb.makeCommit(connection);
                        connection.close();
                    }else out.println("incorrect");
                }
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

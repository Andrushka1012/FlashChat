package FlashChatServer.actions;

import FlashChatServer.ConnectionToDb;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class ActionSendImage implements Action{

    private String action;
    private String image_id;
    private String image_source;
    private String sender_id;
    private String recipient_id;


    @Override
    public void execute(PrintWriter out) {
        java.sql.Connection connection = ConnectionToDb.connectToDB("");
        if (connection != null){
            BufferedWriter writer = null;
            try {
                 File file = new File("C:\\projects\\FlashChatServer\\FlashChatImages",image_id + ".txt");
                //File file = new File("FlashChatImages",image_id + ".txt");
                if (file.exists()) file.delete();
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(image_source);
            } catch (IOException e) {
                e.printStackTrace();
                out.println("error");

            }finally {
                if (writer != null) try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    out.println("error");
                }
            }

            try {
                Statement st = connection.createStatement();
                String s = "SELECT * from Images" +
                        " where img_id = \'" + image_id + "\'";
                ResultSet result = st.executeQuery(s);
                if (!result.next()){
                    String sql = "INSERT INTO Images VALUES (" +
                            "\'" + image_id + "\'," +
                            "\'" + sender_id + "\'," +
                            "\'" + recipient_id + "\')";
                    ResultSet res = st.executeQuery(sql);
                }
                ConnectionToDb.makeCommit(connection);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("error");
            }
        }else{
            System.out.println("no connect to db");
            out.println("error");
        }


    }
}

package FlashChatServer.actions;

import FlashChatServer.Connection;

import java.io.PrintWriter;

public class ActionExit implements Action{
    String jsonString;
    Connection connection;
    public ActionExit(Connection con, String json){
        jsonString = json;
        connection = con;
    }
    @Override
    public void execute(PrintWriter out) {
        Connection.getConnections().remove(connection);
        connection.close();
    }
}

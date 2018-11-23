package FlashChatServer;

import FlashChatServer.actions.Action;
import FlashChatServer.actions.ActionsFactory;

import java.io.PrintWriter;

public class ActionHelper extends Thread {

    private String json;
    private PrintWriter out;
    Connection connection;

    ActionHelper(String json,PrintWriter out,Connection connection){
        this.json = json;
        this.out = out;
        this.connection = connection;
    }

    @Override
    public void run() {
        Action action = ActionsFactory.createAction(connection,json);
        action.execute(out);
    }
}

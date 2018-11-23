package FlashChatServer.actions;

import FlashChatServer.FirebaseUtil;

import java.io.PrintWriter;

public class ActionUnregisterDevice implements Action{
    private String action;
    private String deviceToken;

    @Override
    public void execute(PrintWriter out) {
        FirebaseUtil firebaseUtil = new FirebaseUtil();
        firebaseUtil.token(deviceToken)
                .delete();
        out.println("OK");
    }
}

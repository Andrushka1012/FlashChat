package FlashChatServer.actions;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class FirebaseRequest {
    @Expose
    private String to;
    @Expose
    private String priority = "high";
    @Expose
    private String title;
    @Expose
    private HashMap<String, String> data = new HashMap<>();

    public FirebaseRequest(String to, String title) {
        this.to = to;
        this.title = title;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
}

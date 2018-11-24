package FlashChatServer.actions;

import FlashChatServer.Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.PrintWriter;

public class ActionsFactory {
    public static Action createAction(Connection connection,String JSONString){
        JsonParser jsonParser = new JsonParser();
        JsonObject obj = (JsonObject) jsonParser.parse(JSONString);
        String action = obj.get("action").getAsString();
        System.out.println("Json action:"+action);
        Gson gson = new Gson();


        switch (action){
            case "action_connection":
                return gson.fromJson(JSONString,ActionConnection.class);
            case "exit":
                return new ActionExit(connection,JSONString);
            case "action_login":
                return gson.fromJson(JSONString,ActionLogin.class);
            case "action_register":
                return gson.fromJson(JSONString,ActionRegister.class);
            case "action_LoginFromFacebook":
                return gson.fromJson(JSONString,ActionLoginFromFacebook.class);
            case "action_get_person_date":
                return gson.fromJson(JSONString,ActionGetPersonData.class);
            case "action_save_profile_changes":
                return gson.fromJson(JSONString,ActionSaveProfileChanges.class);
            case "action_send_image":
                return gson.fromJson(JSONString,ActionSendImage.class);
            case "action_load_image":
                return gson.fromJson(JSONString,ActionLoadImage.class);
            case "action_search":
                return gson.fromJson(JSONString,ActionSearch.class);
            case "action_send_message":
                return gson.fromJson(JSONString,ActionSendMessage.class);
            case "action_get_messages":
                return gson.fromJson(JSONString,ActionGetMessages.class);
            case "action_get_online_person_data":
                return gson.fromJson(JSONString,ActionGetOnlinePersonData.class);
            case "action_get_all_messages":
                return gson.fromJson(JSONString,ActionGetAllMessages.class);
            case "action_get_names":
                return gson.fromJson(JSONString,ActionGetNames.class);
            case "action_check_new_messages":
                return gson.fromJson(JSONString,ActionCheckNewMessages.class);
            case "action_change_password":
                return gson.fromJson(JSONString,ActionChanePassword.class);
            case "action_unregister_device":
                return gson.fromJson(JSONString,ActionUnregisterDevice.class);
            case "action_get_image":
                return gson.fromJson(JSONString,ActionGetImage.class);
            case "action_get_new_messages":
                return gson.fromJson(JSONString, ActionGetNewMessages.class);

            default:return out -> { };
        }

    }
}

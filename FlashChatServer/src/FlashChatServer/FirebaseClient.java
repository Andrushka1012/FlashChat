package FlashChatServer;

import FlashChatServer.actions.FirebaseRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface FirebaseClient {

    String TOKEN = "key=AAAA6ArT0cQ:APA91bHZe9bM4fOtq30Pn00lh9N250IwR92fye_3wQD7hdQL0YPzusPGakykTFOEPAr9FMikrB8-rhPn6lkRggVPqsL-dCfL4QIbv3QsILaUJ97Mjs2VNIHbN_yWqY1ritf-GzG3Fa1o";


    @Headers("Authorization: " + TOKEN)
    @POST("/fcm/send")
    public Call<Void> sendMessage(@Body FirebaseRequest reques, @Header("Authorization") String auth);

}

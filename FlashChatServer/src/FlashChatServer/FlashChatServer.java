package FlashChatServer;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FlashChatServer {
    private static final String IP_SERVER = "192.168.0.107";
    private static final int PORT = 50000;


    public static void main(String argS[]) {
        FirebaseUtil.retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerSocket server;
        Socket socket;
        try {
            server = new ServerSocket(PORT);
            while (true) {
                System.out.println(InetAddress.getLocalHost().getHostAddress() + " is waiting");
                socket = server.accept();
                Connection t = new Connection(socket);
                t.start();
                System.out.println("Connected:" + socket.getInetAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* String sql = "select * from images";
        ConnectionToDb.executeQueryToDB(ConnectionToDb.connectToDB(""), sql);*/
    }
}

package FlashChatServer;

import FlashChatServer.actions.Action;
import FlashChatServer.actions.ActionConnection;
import FlashChatServer.actions.ActionExit;
import FlashChatServer.actions.ActionsFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Connection extends Thread{
    private boolean execute = true;
    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;
    private static List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());

    Connection(Socket s){
        socket = s;
        connections.add(this);

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static List<Connection> getConnections() {
        return connections;
    }

    @Override
    public void run() {
        while (execute){
            String jsonString = null;
            try {
                jsonString = in.readLine();
                System.out.println("read String:" + jsonString);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
            if (jsonString == null || jsonString.equals("null")) {
                ActionExit actionExit = new ActionExit(this,"");
                actionExit.execute(out);
                return;
            }
            ActionHelper actionHelper = new ActionHelper(jsonString,out,this);
            actionHelper.run();

        }
        System.out.println("Отключен " + socket.getInetAddress().toString() + ".\nАктивных " + connections.size());

    }
    public void close()  {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        execute = false;
    }

    public String getHostName(){
        return socket.getInetAddress().toString();
    }

}

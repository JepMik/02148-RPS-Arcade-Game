package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

// Chat class
public class Chat implements Runnable {
    Space chat;
    Space infoSpace;
    Space ping;

    public Chat(Space chat, Space infoSpace, Space ping) {
        this.chat = chat;
        this.infoSpace = infoSpace;
        this.ping = ping;
    }

    public void run() {
        ArrayList<String> users = new ArrayList<String>();
        new Thread(new ChatUserUpdater(chat, infoSpace, users, ping)).start();
        while (true) {
            try {
                // Send msg from user to all users
                Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class));
                String output = ">> " + t[0] + ": " + t[1];
                Object[] res = infoSpace.query(new ActualField("Users"), new FormalField(Object.class));
                users = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
                System.out.println(output);
                for (String user : users) {
                    chat.put("output", user, output);
                }
            } catch (InterruptedException e) {}
        }
    }
}

class ChatUserUpdater implements Runnable {
    Space chat;
    Space ping;
    Space infoSpace;
    ArrayList<String> users;
    boolean hasPut = false;

    public ChatUserUpdater(Space chat, Space infoSpace, ArrayList<String> users, Space ping) {
        this.chat = chat;
        this.users = users;
        this.infoSpace = infoSpace;
        this.ping = ping;
    }
    // Adds client to chat, must have username
    public void run() {
        while(true) {
            try {
                String new_user = (String)chat.get(new FormalField(String.class))[0];
                System.out.println(new_user + " logged in!");
                if (hasPut) {
                	Object[] response = infoSpace.get(new ActualField("Users"), new FormalField(Object.class)); //Looking for pong response
                	users = (ArrayList<String>)((ArrayList<String>)response[1]).clone();
                }
                if (!users.contains(new_user)) {
                	users.add(new_user);
                	new Thread(new Ping(ping, infoSpace, new_user)).start();
                }
                infoSpace.put("Users", users);
                hasPut = true;
            } catch (InterruptedException e) {}
        }
    }
}

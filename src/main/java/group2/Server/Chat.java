package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

// Chat class
public class Chat implements Runnable {
    Space chat;
    Space infoSpace;
     ArrayList<String> clients;

    public Chat(Space chat, Space infoSpace, ArrayList<String> clients) {
        this.chat = chat;
        this.infoSpace = infoSpace;
        this.clients = clients;
    }

    @SuppressWarnings("unchecked")
	public void run() {
        while (true) {
            try {
                // Send msg from client to all clients
                Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class));
                String output = ">> " + t[0] + ": " + t[1];
                Object[] res = infoSpace.query(new ActualField("Clients"), new FormalField(Object.class));
                clients = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
                System.out.println(output);
                for (String client : clients) {
                    chat.put("output", client, output);
                }
            } catch (InterruptedException e) {}
        }
    }
}


package group2;

import java.util.ArrayList;

import org.jspace.*;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        // create repository and space
        SpaceRepository repository = new SpaceRepository();
        SequentialSpace chat = new SequentialSpace();
        repository.add("chat", chat);
        repository.addGate("tcp://localhost:9001/?keep");
        System.out.println("Server is up...");

        // Create data structure to store active users
        ArrayList<String> users = new ArrayList<String>();
        new Thread(new UserUpdater(chat, users)).start();

        // Keep reading chat messages and printing them
        while (true) {
            // Save user to users list if it is a new user
            Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class));
            String output = ">> " + t[0] + ": " + t[1];
            System.out.println(output);
            for (String user : users) {
                chat.put("output", user, output);
            }
        }
    }
}

class UserUpdater implements Runnable {

    Space chat;
    ArrayList<String> users;

    public UserUpdater(Space chat, ArrayList<String> users) {
        this.chat = chat;
        this.users = users;
    }

    public void run() {
        while(true) {
            try {
                String new_user = (String)chat.get(new FormalField(String.class))[0];
                System.out.println(new_user + " logged in!");
                if (!users.contains(new_user)) users.add(new_user);
            } catch (InterruptedException e) {}
        }

    }
}
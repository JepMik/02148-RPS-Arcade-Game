package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

// Class used for handling spectators
class SpectatorUserUpdater implements Runnable {
    Space spectators;
    ArrayList<String> users;

    public SpectatorUserUpdater(Space spectators, ArrayList<String> users) {
        this.spectators = spectators;
        this.users = users;
    }

    public void run(){
        while(true) {
            try {
                String new_user = (String)spectators.get(new ActualField("Joined"), new FormalField(String.class))[1];
                System.out.println(new_user + " joined spectators!");
                if (!users.contains(new_user)) users.add(new_user);
            } catch (InterruptedException e) {}
        }
    }

}

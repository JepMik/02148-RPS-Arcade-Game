package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

// Class used for handling spectators
class SpectatorUserUpdater implements Runnable {
    Space spectators;
    ArrayList<String> clients;

    public SpectatorUserUpdater(Space spectators, ArrayList<String> clients) {
        this.spectators = spectators;
        this.clients = clients;
    }

    public void run(){
        while(true) {
            try {
                String new_user = (String)spectators.get(new ActualField("Joined"), new FormalField(String.class))[1];
                System.out.println(new_user + " joined spectators!");
                if (!clients.contains(new_user)) clients.add(new_user);
            } catch (InterruptedException e) {}
        }
    }

}

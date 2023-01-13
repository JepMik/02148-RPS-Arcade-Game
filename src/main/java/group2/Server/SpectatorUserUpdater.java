package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

// Class used for handling spectators
class SpectatorUserUpdater implements Runnable {
    Space spectators;
    Space infoSpace;
    ArrayList<String> clients;

    public SpectatorUserUpdater(Space spectators, Space infoSpace, ArrayList<String> clients) {
        this.spectators = spectators;
        this.infoSpace = infoSpace;
        this.clients = clients;
    }

    public void run(){
        while(true) {
            try {
                String new_user = (String)spectators.get(new ActualField("Joined"), new FormalField(String.class))[1];
                if (!clients.contains(new_user)) {
                    System.out.println(new_user + " joined spectators!");
                    clients.add(new_user);
					infoSpace.put("Broadcast", "Spectators", clients);
                }
            } catch (InterruptedException e) {}
        }
    }

    public void removeUser(String user) throws InterruptedException {
        if (clients.contains(user)) {
            clients.remove(user);
			infoSpace.put("Broadcast", "Spectators", clients);
        }
    }

}

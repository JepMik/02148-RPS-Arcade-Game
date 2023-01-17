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
    boolean hasPut = false;

    public SpectatorUserUpdater(Space spectators, Space infoSpace) {
        this.spectators = spectators;
        this.infoSpace = infoSpace;
        this.clients = new ArrayList<String>();
    }
	@SuppressWarnings("unchecked")
    public void run(){
        while(true) {
            try {
                String new_user = (String)spectators.get(new ActualField("Joined"), new FormalField(String.class))[1];
                if (hasPut) {
					Object[] res = infoSpace.get(new ActualField("Spectators"), new FormalField(Object.class));
					clients = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
                }
                if (!clients.contains(new_user)) {
                    System.out.println(new_user + " joined spectators!");
                    clients.add(new_user);
					infoSpace.put("Broadcast", "Spectators", clients);
                }
        		infoSpace.put("Spectators", clients);
                hasPut = true;
            } catch (InterruptedException e) {}
        }
    }

    public void removeUser(String user) throws InterruptedException {
        Object[] res = infoSpace.get(new ActualField("Spectators"), new FormalField(Object.class));
		clients = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
		if (clients.contains(user)) {
            clients.remove(user);
        }
		infoSpace.put("Broadcast", "Spectators", clients);
        infoSpace.put("Spectators", clients);
    }

    public void movedUser() throws InterruptedException {
        Object[] res = infoSpace.get(new ActualField("Spectators"), new FormalField(Object.class));
		clients = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
		infoSpace.put("Broadcast", "Spectators", clients);
        infoSpace.put("Spectators", clients);
    }

    public ArrayList<String> getSpectators() {
        System.out.println("[SpectatorUserUpdater]Current clients: " + clients);
        return clients;
    }
}

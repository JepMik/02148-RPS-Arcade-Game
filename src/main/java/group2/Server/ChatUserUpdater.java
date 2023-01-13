package group2.Server;

import java.util.ArrayList;
import org.jspace.*;

class ChatUserUpdater implements Runnable {
    Space chat;
    Space ping;
    Space infoSpace;
    ArrayList<String> clients;
    boolean hasPut = false;

    public ChatUserUpdater(Space chat, Space infoSpace, ArrayList<String> clients, Space ping) {
        this.chat = chat;
        this.clients = clients;
        this.infoSpace = infoSpace;
        this.ping = ping;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        while(true) {
            try {
                // Wait for a new username verification request
                String username = (String)chat.get(new FormalField(String.class), new ActualField("login"), new FormalField(Object.class))[0];
                System.out.println("Verifying " + username + "...");

                // Clone/Update Clients list
                if (hasPut) {
                	Object[] response = infoSpace.get(new ActualField("Clients"), new FormalField(Object.class));
                	clients = (ArrayList<String>)((ArrayList<String>)response[1]).clone();
                }
                

				// If username is not unique, send username verification response back to client and wait for a new username
				if (clients.contains(username)) {
		            System.out.println(username + "is already taken!");
		            chat.put(username, "response", "taken");
					infoSpace.put("Clients", clients);
		            continue;
		        }

		        // Send the approved username verification response back to client and add username to clients list
				System.out.println(username + " logged in!");
				clients.add(username);
				infoSpace.put("Clients", clients);

				//Only send valid after user has been added
		        chat.put(username, "response", "valid");

		    	new Thread(new Ping(ping, infoSpace, username)).start();
                hasPut = true;
            } catch (InterruptedException e) {}
        }
    }
}




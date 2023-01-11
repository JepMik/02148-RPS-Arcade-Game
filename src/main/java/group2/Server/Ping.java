package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

// Instantiated once per Client 
class Ping implements Runnable {
	Space ping;
	Space infoSpace;
	String username;
	
	public Ping(Space ping, Space infoSpace, String username) {
		this.ping = ping;
		this.infoSpace = infoSpace;
		this.username = username;
	}
	
	public void run() {
		try {
			while(true) {
				// Sleep to reduce message traffic
				Thread.sleep(3000);
				// Exit if client is unresponsive or wants to exit
				Object[] response = ping.getp(new ActualField(username), new FormalField(String.class));
				if (response == null) {
					break;
				} else if (((String)response[1]).equals("break")) {
					break;
				}
				
				// Ping client
				ping.put(username, "ping");
			}
			removeClient();
		} catch (InterruptedException e) {}
	}
	
	@SuppressWarnings("unchecked")
	private void removeClient() throws InterruptedException {
		System.out.println("Removing " + username + "...");
		ArrayList<String> clients = (ArrayList<String>)infoSpace.get(new ActualField("Clients"), new FormalField(Object.class))[1];
		clients.remove(username);
		infoSpace.put("Clients", clients);
		infoSpace.put("Removed", username);
	}
}



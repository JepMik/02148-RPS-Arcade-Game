package group2.Client;

import org.jspace.Space;
import org.jspace.RemoteSpace;
import org.jspace.ActualField;

// Pings the Server to confirm the connection status
public class Pong implements Runnable {

    Space ping;
    Space active;
    String username;
    
    
    public Pong(RemoteSpace ping, Space active, String username) {
    	this.ping = ping;
        this.active = active;
        this.username = username;
    }

    public void run(){
        try {
			while(wantToContinue()) {
				ping.put(username, "continue");
			    ping.get(new ActualField(username), new ActualField("ping"));
			}
			ping.put(username, "break");
		} catch (InterruptedException e) {}
    }
    
    private boolean wantToContinue() throws InterruptedException {
    	return (active.queryp(new ActualField("active")) != null);
    }
}
    

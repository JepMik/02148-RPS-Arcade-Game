package group2.Client;

import org.jspace.Space;
import org.jspace.FormalField;
import org.jspace.ActualField;

// This class listens for changes/messages from the server and sends to the GUI
public class ServerListener implements Runnable {
    private Space serverInfo;
    private Space GUISpace;
    private String username;

    public ServerListener(Space serverInfo, Space GUISpace, String username) {
        this.serverInfo = serverInfo;
        this.GUISpace = GUISpace;
        this.username = username;
    }

    public void run(){
		while (true) {
            try {
				Object[] tuple = serverInfo.get(new FormalField(String.class), new ActualField(username), new FormalField(Object.class));
				switch ((String)tuple[0]) {
                    case "Score":
                        // Sends username and score to GUI
                        String player2 = ((String[])tuple[2])[1];
                        if (username.equals(player2)) {
                        	GUISpace.put("ToGui", "Current score", new String[]{((String[])tuple[2])[3], ((String[])tuple[2])[2]});
                        } else {
                        	GUISpace.put("ToGui", "Current score", new String[]{((String[])tuple[2])[2], ((String[])tuple[2])[3]});
                        }
                    	break;
                    case "New message":
                        // Sends message to GUI
                        GUISpace.put("ToGui", "New message", tuple[2]);
                        break;

                }
            } catch (InterruptedException e) {}
        }
    }
}

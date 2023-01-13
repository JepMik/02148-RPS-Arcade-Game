package group2.Client;

import org.jspace.*;
import group2.Common.RPS;
import group2.GUI.GameGUI;
import group2.GUI.LoginGUI;

import java.io.IOException;
import java.util.Scanner;


public class Client {

	private static RemoteSpace chat;
	private static RemoteSpace ping;
	private static RemoteSpace spectators;
	private static RemoteSpace playing;
    private static RemoteSpace serverInfo;

    public static void main(String[] args) throws InterruptedException {

        //Open main menu
        Space GUISpace = new SequentialSpace();
        LoginGUI loginGui = new LoginGUI(GUISpace);

		// Get and verify IP
		getVerifyIP(GUISpace);
		
		Space active = new SequentialSpace(1);

		// Get and verify username
		String username = getVerifyUsername(GUISpace, chat);

        new Thread(new GameGUI(GUISpace, username)).start();

        // Add tokens to spaces
	    active.put("active");
	    spectators.put("Joined", username);
		spectators.put("Ready", username);

        // Starting threads and add listener to spectators
        new Thread(new ServerListener(serverInfo, GUISpace,username)).start();
        new Thread(new Pong(ping, active, username)).start();
        SpectatorsListener listener = new SpectatorsListener(spectators, username);
        new Thread(listener).start();
        GameListener gameListener = new GameListener(playing, spectators, GUISpace, username, listener);
        new Thread(gameListener).start();

        System.out.println("Everything is running");

        // Client game logic
        while(true) {
        	Object[] tuple = GUISpace.get(new ActualField("ToClient"), new FormalField(String.class), new FormalField(Object.class));
        	switch ((String)tuple[1]) {
        		case "Move":
        			if (!gameListener.getPlayed() && listener.isInGame()) {
						System.out.println("Sent choice " + ((String[])tuple[2])[0]);
        				playing.put(username, new RPS(((String[])tuple[2])[0]));
        				gameListener.setPlayed(true);
					}
        			break;
        		case "Send message":
        			chat.put(username, tuple[2]);
        			break;
        	}
        }
     }
    // Method verifies input IP
    private static void getVerifyIP(Space GUISpace) throws InterruptedException {
        while (true) {
            String ip = (String)GUISpace.get(new ActualField("IP"), new FormalField(String.class))[1];
            // Connect to spaces
			try {
                chat = new RemoteSpace("tcp://" + ip + ":9001/chat?keep");
			    ping = new RemoteSpace("tcp://" + ip + ":9001/ping?keep");
			    spectators = new RemoteSpace("tcp://" + ip + ":9001/spectators?keep");
			    playing = new RemoteSpace("tcp://" + ip + ":9001/playing?keep");
			    serverInfo = new RemoteSpace("tcp://" + ip + ":9001/serverInfo?keep");
			    break;
			} catch (IOException e) {
        		GUISpace.put("IP Response", "Fail");
            }
        }
        GUISpace.put("IP Response", "Ok");
    }
    // Method verifies input username
    private static String getVerifyUsername(Space GUISpace, Space chat) throws InterruptedException {
        // Get inputted username and repeat if verification fails
        String username;
	    while (true) {
            username = (String)GUISpace.get(new ActualField("Username"), new FormalField(String.class))[1];
            // Put username in chat space to be verified
            System.out.println("Trying to login with " + username);
	    	chat.put(username, "login", "filler");
	    	// Try again if the verification fails
	    	String res = (String)chat.get(new ActualField(username), new ActualField("response"), new FormalField(String.class))[2];
	    	if (res.equals("valid")) {
                break;
            }
            GUISpace.put("Name Response", "Fail");
        }
		GUISpace.put("Name Response", "Ok");
    	return username;
    }

}

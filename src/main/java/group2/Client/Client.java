package group2.Client;

import org.jspace.*;
import group2.Common.RPS;
import group2.GUI.LoginGUI;

import java.io.IOException;
import java.util.Scanner;


public class Client {

	private static RemoteSpace chat;
	private static RemoteSpace ping;
	private static RemoteSpace spectators;
	private static RemoteSpace playing;

    public static void main(String[] args) throws InterruptedException {

        //Open main menu
        Space GUISpace = new SequentialSpace();
        LoginGUI loginGui = new LoginGUI(GUISpace);

		// Get and verify IP
		getVerifyIP(GUISpace);
		
		Space active = new SequentialSpace(1);
		    
		// Get and verify username
		String username = getVerifyUsername(GUISpace, chat);

        // Add tokens to spaces
	    active.put("active");
	    spectators.put("Joined", username);
		spectators.put("Ready", username);

        // Starting threads and add listener to spectators
        new Thread(new ChatListener(chat, username)).start();
        new Thread(new Pong(ping, active, username)).start();
        SpectatorsListener listener = new SpectatorsListener(spectators, username);
        new Thread(listener).start();
        new Thread(new GameListener(playing, spectators, username, listener)).start();

        System.out.println("Say something...");

        // Client game logic
        while(true) {
            String message = (String)GUISpace.get(new ActualField("CMD"), new FormalField(String.class))[1];
            if (message.startsWith("say: ")) {
                chat.put(username, message.substring(5));
            } else if (message.startsWith("play: ") && listener.isInGame()) {
                playing.put(username, new RPS(message.split(" ")[1]));
            }
        }
    }
    
    private static void getVerifyIP(Space GUISpace) throws InterruptedException {
        while (true) {
            String ip = (String)GUISpace.get(new ActualField("IP"), new FormalField(String.class))[1];
            // Connect to spaces
			try {
                chat = new RemoteSpace("tcp://" + ip + ":9001/chat?keep");
			    ping = new RemoteSpace("tcp://" + ip + ":9001/ping?keep");
			    spectators = new RemoteSpace("tcp://" + ip + ":9001/spectators?keep");
			    playing = new RemoteSpace("tcp://" + ip + ":9001/playing?keep");
			    break;
			} catch (IOException e) {
                System.out.println("IP fail");
        		GUISpace.put("IP Response", "Fail");
            }
        }
        GUISpace.put("IP Response", "Ok");
    }

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
            System.out.println("Name fail");
            GUISpace.put("Name Response", "Fail");
        }
		GUISpace.put("Name Response", "Ok");
    	return username;
    }

}

package group2.Client;

import org.jspace.*;
import group2.Common.RPS;

import java.io.IOException;
import java.util.Scanner;


public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
		// Input scanner
		Scanner input = new Scanner(System.in);
		
		// Get IP to connect to host
		System.out.print("Please enter the room's IP address (or localhost to play locally): ");
		String ip = input.nextLine();
		
		System.out.print("Please enter your username: ");
		String username = input.nextLine();
		
		// Create spaces
	    RemoteSpace chat = new RemoteSpace("tcp://" + ip + ":9001/chat?keep");
	    RemoteSpace ping = new RemoteSpace("tcp://" + ip + ":9001/ping?keep");
	    RemoteSpace spectators = new RemoteSpace("tcp://" + ip + ":9001/spectators?keep");
	    RemoteSpace playing = new RemoteSpace("tcp://" + ip + ":9001/playing?keep");
	    Space active = new SequentialSpace(1);
	    
	    // Add tokens to spaces
	    active.put("active");
	    chat.put(username);
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
            String message = input.nextLine();
            if (message.startsWith("say: ")) {
                chat.put(username, message.substring(5));
            } else if (message.startsWith("play: ") && listener.isInGame()) {
                playing.put(username, new RPS(message.split(" ")[1]));
            }
        }
    }
}

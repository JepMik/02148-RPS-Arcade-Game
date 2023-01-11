package group2;

import org.jspace.*;

import java.io.IOException;
import java.util.Scanner;


public class Client {
	
    public static void main(String[] args) throws IOException, InterruptedException {
        // creating spaces
        Scanner input = new Scanner(System.in);

        System.out.print("Please enter the room's IP address (or localhost to play locally): ");
        String ip = input.nextLine();

        RemoteSpace chat = new RemoteSpace("tcp://" + ip + ":9001/chat?keep");
        RemoteSpace ping = new RemoteSpace("tcp://" + ip + ":9001/ping?keep");
        RemoteSpace spectators = new RemoteSpace("tcp://" + ip + ":9001/spectators?keep");
        RemoteSpace playing = new RemoteSpace("tcp://" + ip + ":9001/playing?keep");
        
        // Space to keep Pong active
        Space active = new SequentialSpace(1);
        active.put("active");
        

        // gets username and put client in different spaces
        System.out.print("Please enter your username: ");
        String username = input.nextLine();
        chat.put(username);
        spectators.put("Joined", username);
        spectators.put("Ready", username);

        //starting threads and add listener to spectators
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
                //TODO spawn new playing thread

            }
        }
    }
}

//RPS game loop, sending and receiving info from the game space
class GameListener implements Runnable{

    private int[] points;
    Space playing, spectators;
    String username;
    SpectatorsListener listener;

    public GameListener(RemoteSpace playing, RemoteSpace spectators, String username, SpectatorsListener listener) {
        this.playing = playing;
        this.spectators = spectators;
        this.username = username;
        this.listener = listener;
    }

    public void run() {
        while(true) {
            try{
                //If start of game
                String opponent = (String)playing.get(new ActualField(username), new FormalField(String.class))[1];
                points = new int[]{0, 0};
                if (opponent.equals("Disconnected")) {
                    System.out.println("Other user disconnected");
                    listener.leaveGame();
                    spectators.put("Joined", username);
                    spectators.put("Ready", username);
                }
                System.out.println("Playing against " + opponent);
                //If in game
                while (true) {
                    String winner = (String)playing.get(new ActualField(username), new FormalField(String.class))[1];
                    if (winner.equals("disconnected")) {
                        System.out.println("Other user disconnected");
                        listener.leaveGame();
                        spectators.put("Joined", username);
                        spectators.put("Ready", username);
                        break;
                    } else if (winner.equals("draw")) {
                        System.out.println("It is a draw");
                    } else {
                        System.out.println(winner + " won this round!");
                        if (winner.equals(username)) {
                            points[0]++;
                        } else {
                            points[1]++;
                        }
                        //If game is over
                        if (points[0] == 2 || points[1] == 2) {
                            if (points[0] == 2) {
                                System.out.println("We won!");
                            } else {
                                System.out.println("We lost...");
                        		listener.leaveGame();
                                spectators.put("Joined", username);
                                spectators.put("Ready", username);
                            }
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {}
        }
    }

}

//Listen to spectators space for info that we are moved into a game
class SpectatorsListener implements Runnable{

    private boolean inGame = false;
    Space spectators;
    String username;

    public SpectatorsListener(RemoteSpace spectators, String username) {
        this.spectators = spectators;
        this.username = username;
    }

    public void run() {
        while(true) {
            try{
                Object[] output = spectators.get(new ActualField(username));
                inGame = true;
            } catch (InterruptedException e) {}
        }
    }

    public void leaveGame() {
        inGame = false;
    }

    public boolean isInGame() {
        return inGame;
    }
}

//Listen to chat space for messages from other users
class ChatListener implements Runnable{

    Space chat;
    String username;

    public ChatListener(RemoteSpace chat, String username) {
        this.chat = chat;
        this.username = username;
    }

    public void run() {
        while(true) {
            try{
                Object[] output = chat.get(new ActualField("output"), new ActualField(username), new FormalField(String.class));
                System.out.println((String)output[2]);
            } catch (InterruptedException e) {}
        }
    }

}

// Pings the Server to confirm the connection status
class Pong implements Runnable {

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
			    System.out.println(username + " got ping!");
			}
			ping.put(username, "break");
		} catch (InterruptedException e) {}
    }
    
    private boolean wantToContinue() throws InterruptedException {
    	return (active.queryp(new ActualField("active")) != null);
    }

}


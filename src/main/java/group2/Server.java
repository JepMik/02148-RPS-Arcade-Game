package group2;

import java.util.ArrayList;
import java.util.HashMap;


import org.jspace.*;

enum Choice {
    ROCK, PAPER, SCISSORS
}

public class Server {

    public static void main(String[] args) throws InterruptedException {
        // creating spaces for server
        SpaceRepository repository = new SpaceRepository();
        SequentialSpace chat = new SequentialSpace();
        SequentialSpace playing = new SequentialSpace(2);
        QueueSpace spectators = new QueueSpace();
        // Sends game info between classes on the server
		SequentialSpace gameInfoSpace = new SequentialSpace();

        // adding to repository
        repository.add("chat", chat);
        repository.add("playing", playing);
        repository.add("spectators", spectators);
        repository.addGate("tcp://10.209.95.117:9001/?keep");
        System.out.println("Server is up...");

        new Thread(new Chat(chat)).start();
        Game game = new Game(playing, gameInfoSpace);
        new Thread(game).start();
        //Store spectator usernames in ArrayList
        ArrayList<String> users = new ArrayList<String>();
        new Thread(new SpectatorUserUpdater(spectators, users)).start();


        // Game loop
		while (true) {
			//wait until need new player
			gameInfoSpace.get(new ActualField("needPlayer"));
			//wait until there is a player in the queue
			spectators.query(new ActualField("Ready"), new FormalField(String.class));
            //Move in to fill space
            if (game.connectedPlayers() < 2) { // If there is two players in playing space
                //Move in and
                System.out.println("Moved user " + users.get(0) + " from queue to game");
                spectators.get(new ActualField("Ready"), new FormalField(String.class));
                spectators.put(users.get(0));
                game.addPlayer(users.get(0));
                users.remove(0);
            }
        }
    }
}

// RPS (ROCK,PAPER,SCISSORS)
class RPS {

    Choice choice;

    // RPS constructor using enums
	public RPS(String choice) { // 0 = rock, 1 = paper, 2 = scissors
        switch (choice.toLowerCase()) {
            case "rock":
                this.choice = Choice.ROCK;
                break;
            case "paper":
                this.choice = Choice.PAPER;
                break;
            case "scissors":
                this.choice = Choice.SCISSORS;
                break;
        }
    }

    // Method that decides winner
	public int winner(RPS other) {
		int winner = 2;
		switch (choice) {
            case ROCK:
                if (other.choice == Choice.SCISSORS) {
                    winner = 0;
                } else if (other.choice == Choice.PAPER) {
                    winner = 1;
                }
            	break;
            case PAPER:
                if (other.choice == Choice.ROCK) {
                    winner = 0;
                } else if (other.choice == Choice.SCISSORS) {
                    winner = 1;
                }
            	break;
            case SCISSORS:
                if (other.choice == Choice.PAPER) {
                    winner = 0;
                } else if (other.choice == Choice.ROCK) {
                    winner = 1;
                }
            	break;
        }
		return winner;
    }
}

// Game class
class Game implements Runnable {
    Space playing, gameInfoSpace;
    private int connected = 0;
    private ArrayList<String> users = new ArrayList<>();
    private HashMap<String, Integer> scoreboard = new HashMap<>();

    //Game constructor
	public Game(Space playing, Space gameInfoSpace) throws InterruptedException {
		this.playing = playing;
		this.gameInfoSpace = gameInfoSpace;
		gameInfoSpace.put("needPlayer");
		gameInfoSpace.put("needPlayer");
    }

    public void run() {
        try {
            while (true) {
	            if (connected < 2) {
	                gameInfoSpace.get(new ActualField("gotPlayers"));
	            }
	            //Send name of oppenent
	            System.out.println("New game started: " + users.get(0) + " vs " + users.get(1));
	            playing.put(users.get(0), users.get(1));
	            playing.put(users.get(1), users.get(0));

				//Determine who wins thisround x3
				int[] points = new int[]{0, 0};
				while (points[0] != 2 && points[1] != 2) {
					//Get choice from each player
					RPS[] choices = new RPS[2];
					for (int i = 0; i < 2; i++) {
			            RPS choice = (RPS)playing.get(new ActualField(users.get(i)), new FormalField(RPS.class))[1];
			            choices[i] = choice;
			        }
					int winner = choices[0].winner(choices[1]);
					if (winner == 2) {
						playing.put(users.get(0), "draw");
						playing.put(users.get(1), "draw");
	                } else {
						playing.put(users.get(0), users.get(winner));
						playing.put(users.get(1), users.get(winner));
						points[winner]++;
	                }
	            }
		        // Update score of winner or create score if none has been made
		        int idx = points[0] > points[1] ? 0 : 1;
		        scoreboard.put(users.get(idx), scoreboard.getOrDefault(users.get(idx), 0)+1);
				users.remove(1-idx);
				connected--;
				gameInfoSpace.put("needPlayer");
            }
		} catch (InterruptedException e) {}
    }

    // Adds players and tells gameInfoSpace when there is enough players for game
    public void addPlayer(String name) throws InterruptedException {
        users.add(name);
        connected++;
        if (connected == 2) {
			gameInfoSpace.put("gotPlayers");
        }
    }

    public int connectedPlayers() {
        return connected;
    }
}

// Class used for handling spectators
class SpectatorUserUpdater implements Runnable {
    Space spectators;
    ArrayList<String> users;

    public SpectatorUserUpdater(Space spectators, ArrayList<String> users) {
        this.spectators = spectators;
        this.users = users;
    }

    public void run(){
        while(true) {
            try {
                String new_user = (String)spectators.get(new ActualField("Joined"), new FormalField(String.class))[1];
                System.out.println(new_user + " joined game!");
                if (!users.contains(new_user)) users.add(new_user);
            } catch (InterruptedException e) {}
        }
    }

}

// Chat class
class Chat implements Runnable {
    Space chat;

    public Chat(Space chat) {
        this.chat = chat;
    }

    public void run() {
        ArrayList<String> users = new ArrayList<String>();
        new Thread(new ChatUserUpdater(chat, users)).start();
        while (true) {
            try {
	            // Send msg from user to all users
	            Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class));
	            String output = ">> " + t[0] + ": " + t[1];
	            System.out.println(output);
	            for (String user : users) {
                    chat.put("output", user, output);
	            }
        	} catch (InterruptedException e) {}
        }

    }
}

class ChatUserUpdater implements Runnable {
    Space chat;
    ArrayList<String> users;

    public ChatUserUpdater(Space chat, ArrayList<String> users) {
        this.chat = chat;
        this.users = users;
    }
    // Adds client to chat, must have username
    public void run() {
        while(true) {
            try {
                String new_user = (String)chat.get(new FormalField(String.class))[0];
                System.out.println(new_user + " logged in!");
                if (!users.contains(new_user)) users.add(new_user);
            } catch (InterruptedException e) {}
        }
    }
}
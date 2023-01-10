package group2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.jspace.*;

enum Choice {
    ROCK, PAPER, SCISSORS, DISCONNECTED
}

public class Server {

    public static void main(String[] args) throws InterruptedException {
        // creating spaces for server
        SpaceRepository repository = new SpaceRepository();
        SequentialSpace chat = new SequentialSpace();
        SequentialSpace playing = new SequentialSpace(2);
        SequentialSpace ping = new SequentialSpace();
        SequentialSpace spectators = new SequentialSpace();
        // Sends game info between classes on the server
        SequentialSpace infoSpace = new SequentialSpace();

        // adding to repository
        repository.add("chat", chat);
        repository.add("playing", playing);
        repository.add("spectators", spectators);
        repository.add("ping", ping);


        Scanner input = new Scanner(System.in);
        System.out.print("Please enter the room's IP address (or localhost to play locally): ");
        String ip = input.nextLine();

        repository.addGate("tcp://" + ip + ":9001/?keep");
        System.out.println("Server is up...");

        new Thread(new Chat(chat, infoSpace)).start();

        ArrayList<String> spectatingUsers = new ArrayList<String>();

        Game game = new Game(playing, infoSpace);
        new Thread(game).start();
        new Thread(new SpectatorUserUpdater(spectators, spectatingUsers)).start();

        new Thread(new Mover(infoSpace, spectators, game, spectatingUsers)).start();
        new Thread(new Ping(ping, infoSpace)).start();

        while(true) {
            //If user to remove
            String user = (String)infoSpace.get(new ActualField("Removed"), new FormalField(String.class))[1];
            chat.getAll(new ActualField("output"), new ActualField(user), new FormalField(String.class));
			game.removePlayer(user);
        }
    }
}

class Mover implements Runnable {
    Space InfoSpace;
    Space spectators;
    Game game;
    ArrayList<String> users;

    public Mover(Space InfoSpace, Space spectators, Game game, ArrayList<String> users) {
        this.InfoSpace = InfoSpace;
        this.spectators = spectators;
        this.game = game;
        this.users = users;
    }

    public void run() {
        while(true) {
            try {
                //wait until need new player
                InfoSpace.get(new ActualField("needPlayer"));
                //wait until there is a player in the queue
                spectators.query(new ActualField("Ready"), new FormalField(String.class));
                //Move in to fill space
                if (game.connectedPlayers() < 2) { // If there is two players in playing space
                    //Move in and
                    System.out.println("Moved user " + users.get(0) + " from spectators to game");
                    spectators.get(new ActualField("Ready"), new FormalField(String.class));
                    spectators.put(users.get(0));
                    game.addPlayer(users.get(0));
                    users.remove(0);
                }
            } catch (InterruptedException e) {}
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
            default:
            	this.choice = Choice.ROCK;
            	break;
        }
    }

    public RPS(Choice choice) {
        this.choice = choice;
    }

    public Choice getChoice() {
        return choice;
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
    Space playing, infoSpace;
    private int connected = 0;
    private ArrayList<String> users = new ArrayList<>();
    private HashMap<String, Integer> scoreboard = new HashMap<>();

    //Game constructor
    public Game(Space playing, Space infoSpace) throws InterruptedException {
        this.playing = playing;
        this.infoSpace = infoSpace;
        infoSpace.put("needPlayer");
        infoSpace.put("needPlayer");
    }

    public void run() {
        try {
            gameLoop: while (true) {
                System.out.println("Loop begin");
                if (connected < 2) {
                    infoSpace.get(new ActualField("gotPlayers"));
                }
            	//Empty playing space
            	playing.getAll(new FormalField(Object.class), new FormalField(Object.class));

                //Send name of opponent
                System.out.println("New game started: " + users.get(0) + " vs " + users.get(1));
                playing.put(users.get(0), users.get(1));
                playing.put(users.get(1), users.get(0));

                //Determine who wins this round x3
                int[] points = new int[]{0, 0};
                while (points[0] != 2 && points[1] != 2) {
                    //Get choice from each player
                    RPS[] choices = new RPS[2];
                    for (int i = 0; i < 2; i++) {
                        Object[] res = playing.get(new FormalField(String.class), new FormalField(RPS.class));
                        String name = (String)res[0];
                        RPS choice = (RPS)res[1];
                        if (choice.getChoice() == Choice.DISCONNECTED) {
                            continue gameLoop;
                        }
                        choices[users.indexOf(name)] = choice;
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
                infoSpace.put("needPlayer");
            }
        } catch (InterruptedException e) {}
    }

    // Adds players and tells gameInfoSpace when there is enough players for game
    public void addPlayer(String name) throws InterruptedException {
        users.add(name);
        connected++;
        if (connected == 2) {
            infoSpace.put("gotPlayers");
        }
    }

    // Used for handling removing a player, if player disconnects and game needs restart
    public void removePlayer(String name) throws InterruptedException {
		if (users.contains(name)) {
            connected = 0;
			if (users.size() == 2) {
                playing.put(name, 							  new RPS(Choice.DISCONNECTED));
				playing.put(users.get(1-users.indexOf(name)), new RPS(Choice.DISCONNECTED)); // To game

				playing.put(users.get(1-users.indexOf(name)), "disconnected"); // To user who didn't disconnected
				infoSpace.put("needPlayer");
            }
            infoSpace.put("needPlayer");
            users.clear();
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
                System.out.println(new_user + " joined spectators!");
                if (!users.contains(new_user)) users.add(new_user);
            } catch (InterruptedException e) {}
        }
    }

}

// Chat class
class Chat implements Runnable {
    Space chat;
    Space infoSpace;

    public Chat(Space chat, Space infoSpace) {
        this.chat = chat;
        this.infoSpace = infoSpace;
    }

    public void run() {
        ArrayList<String> users = new ArrayList<String>();
        new Thread(new ChatUserUpdater(chat, infoSpace, users)).start();
        while (true) {
            try {
                // Send msg from user to all users
                Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class));
                String output = ">> " + t[0] + ": " + t[1];
                Object[] res = infoSpace.query(new ActualField("Users"), new FormalField(Object.class));
                users = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
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
    Space infoSpace;
    ArrayList<String> users;
    boolean hasPut = false;

    public ChatUserUpdater(Space chat, Space infoSpace, ArrayList<String> users) {
        this.chat = chat;
        this.users = users;
        this.infoSpace = infoSpace;
    }
    // Adds client to chat, must have username
    public void run() {
        while(true) {
            try {
                String new_user = (String)chat.get(new FormalField(String.class))[0];
                System.out.println(new_user + " logged in!");
                if (hasPut) {
					Object[] response = infoSpace.get(new ActualField("Users"), new FormalField(Object.class)); //Looking for pong response
                	users = (ArrayList<String>)((ArrayList<String>)response[1]).clone();
                }
                if (!users.contains(new_user)) users.add(new_user);
                infoSpace.put("Users", users);
                hasPut = true;
            } catch (InterruptedException e) {}
        }
    }
}

// Class Ping
class Ping implements Runnable {
    Space ping;
    Space infoSpace;
    ArrayList<String> users;

    public Ping(Space ping, Space infoSpace) {
        this.ping = ping;
        this.infoSpace = infoSpace;
    }

    public void run() {
        while(true) {
            try {
                // Ping all users
                // Get all users from infoSpace
                Object[] res = infoSpace.query(new ActualField("Users"), new FormalField(Object.class));
                users = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
                for (String user : users) {
                    ping.put(user, "ping");
                }
                // Sleep to reduce message traffic
                Thread.sleep(5000);
                // Get ping responses from all users
                ArrayList<String> removedUsers = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    Object[] pingResponse = ping.getp(new ActualField(users.get(i)), new ActualField("pong"));
                    // Remove user if it did not send the ping response
                    if (pingResponse == null) {
                        System.out.println(users.get(i) + " was unresponsive. Removed from users list.");
                        removedUsers.add(users.get(i));
                        removeUser(users.get(i));
                        i--;
                    }
                }
                res = infoSpace.get(new ActualField("Users"), new FormalField(Object.class));
                for (String user : (ArrayList<String>)res[1]) {
					if (!users.contains(user) && !removedUsers.contains(user)) {
                        users.add(user);
                    }
                }
                infoSpace.put("Users", users);
            } catch (InterruptedException e) {}
        }
    }

    public void removeUser(String user) throws InterruptedException {
        users.remove(user);
        infoSpace.put("Removed", user);
        // Remove from ping space
        ping.getp(new ActualField(user), new FormalField(String.class));

    }
}


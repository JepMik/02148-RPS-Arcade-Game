package group2.Client;
import java.util.ArrayList;
import java.util.HashMap;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;
import org.jspace.FormalField;
import org.jspace.Space;

import group2.Common.Choice;
import group2.Common.RPS;

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
                        RPS choice = (RPS)playing.get(new ActualField(users.get(i)), new FormalField(RPS.class))[1];
                        if (choice.getChoice() == Choice.DISCONNECTED) {
                            continue gameLoop;
                        }
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

				playing.put(users.get(1-users.indexOf(name)), "disconnected"); // To user who disconnected
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
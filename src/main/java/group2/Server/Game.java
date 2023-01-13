package group2.Server;

import java.util.ArrayList;
import java.util.HashMap;

import org.jspace.Space;

import group2.Common.Choice;
import group2.Common.RPS;

import org.jspace.ActualField;
import org.jspace.FormalField;


// Game class
class Game implements Runnable {
    Space playing, infoSpace;
    private int connected = 0;
    private ArrayList<String> clients = new ArrayList<>();
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

                System.out.println("New game started: " + clients.get(0) + " vs " + clients.get(1));

                //Send names of players
                infoSpace.put("Broadcast", "MatchStart", new String[]{clients.get(0), clients.get(1)});

                // Reset points
                infoSpace.put("Broadcast", "Current score", new String[]{clients.get(0), clients.get(1), "0", "0"});

                //Determine who wins this round x3
                int[] points = new int[]{0, 0};
                while (points[0] != 2 && points[1] != 2) {
                    //Get choice from each player
                    RPS[] choices = new RPS[2];
                    for (int i = 0; i < 2; i++) {
                        Object[] res = playing.get(new FormalField(String.class), new FormalField(RPS.class));
                        String name = (String)res[0];
                        RPS choice = (RPS)res[1];
                        System.out.println(i + ":" + name + " choose " + choice.getChoice());
                        if (choice.getChoice() == Choice.DISCONNECTED) {
                            continue gameLoop;
                        }
                        choices[clients.indexOf(name)] = choice;
                    }
                    System.out.println("Both player checked");
                    int winner = choices[0].winner(choices[1]);
                    if (winner == 2) {
                        playing.put(clients.get(0), "draw");
                        playing.put(clients.get(1), "draw");
                    } else {
                        playing.put(clients.get(0), clients.get(winner));
                        playing.put(clients.get(1), clients.get(winner));
                        points[winner]++;
                        infoSpace.put("Broadcast", "Current score", new String[]{clients.get(0), clients.get(1), Integer.toString(points[0]), Integer.toString(points[1])});
                    }

                }
                // Update score of winner or create score if none has been made
                int idx = points[0] > points[1] ? 0 : 1;
                scoreboard.put(clients.get(idx), scoreboard.getOrDefault(clients.get(idx), 0)+1);
                infoSpace.put("Broadcast", "Scoreboard", scoreboard.toString());
                clients.remove(1-idx);
                connected--;
                infoSpace.put("needPlayer");
            }
        } catch (InterruptedException e) {}
    }

    // Adds players and tells gameInfoSpace when there is enough players for game
    public void addPlayer(String name) throws InterruptedException {
        clients.add(name);
        connected++;
        if (connected == 2) {
            infoSpace.put("gotPlayers");
        }
    }

    // Used for handling removing a player, if player disconnects and game needs restart
    public void removePlayer(String name) throws InterruptedException {
		System.out.println("Removing? " + name);
		System.out.println("In match " + clients.toString());
		if (clients.contains(name)) {
            System.out.println("Removing user " + name + " from the game with " + clients.size());
            //Game ongoing even though both/one of the players is disconnected
            connected = 0;
			if (clients.size() == 2) {
                playing.put(name, 							  	  new RPS(Choice.DISCONNECTED));
				playing.put(clients.get(1-clients.indexOf(name)), new RPS(Choice.DISCONNECTED)); // To game

				playing.put(clients.get(1-clients.indexOf(name)), "disconnected"); // To user who disconnected
				infoSpace.put("needPlayer");
            }
            infoSpace.put("needPlayer");
            clients.clear();
        }
    }

    public int connectedPlayers() {
        return connected;
    }
}

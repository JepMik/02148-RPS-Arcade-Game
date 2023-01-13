package group2.Client;
import java.util.ArrayList;
import java.util.HashMap;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;
import org.jspace.FormalField;
import org.jspace.Space;

import group2.Common.Choice;
import group2.Common.RPS;

//RPS game loop, sending and receiving info from the game space
class GameListener implements Runnable{

    private int[] points;
    private boolean played = false;
    Space playing, spectators, GUISpace;
    String username;
    SpectatorsListener listener;

    public GameListener(RemoteSpace playing, RemoteSpace spectators, Space GUISpace, String username, SpectatorsListener listener) {
        this.playing = playing;
        this.spectators = spectators;
        this.GUISpace = GUISpace;
        this.username = username;
        this.listener = listener;
    }

    public void run() {
        while(true) {
            try{
                //If start of game
                Object[] matchInfo = playing.get(new ActualField("MatchStart"), new ActualField(username), new FormalField(String.class), new FormalField(String.class));

                String player1 = (String)matchInfo[2];
                String player2 = (String)matchInfo[3];

                System.out.println("A new match has started: " + player1 + " vs " + player2);

                if (player2.equals("Disconnected")) {
                    System.out.println("Other user disconnected");
                    listener.leaveGame();
	                spectators.put("Joined", username);
	                spectators.put("Ready", username);
                    continue;
                } else {
                	GUISpace.put("ToGui", "Playing against", new String[]{player1, player2});
                }
                if (!player1.equals(username)) {
                    continue;
                }
                points = new int[]{0, 0};
                System.out.println("Playing against " + player2);
                // If in game
                while (true) {
                    String winner = (String)playing.get(new ActualField(username), new FormalField(String.class))[1];
                    played = false;
                    if (winner.equals("disconnected")) {
                        System.out.println("Other user disconnected");
                        GUISpace.put("ToGui", "Playing against", new String[]{"disconnected", "disconnected"});
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
                        GUISpace.put("Current score", new int[]{points[0], points[1]});
                        System.out.println(points[0] + " vs " + points[1]);
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

    public boolean getPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

}
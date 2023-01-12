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
                String opponent = (String)playing.get(new ActualField(username), new FormalField(String.class))[1];
                points = new int[]{0, 0};
                if (opponent.equals("Disconnected")) {
                    System.out.println("Other user disconnected");
                    listener.leaveGame();
                }
                System.out.println("Playing against " + opponent);
                GUISpace.put("ToGui", "Playing against", new String[]{username, opponent});
                // If in game
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
                        GUISpace.put("Current score", new int[]{points[0], points[1]});
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
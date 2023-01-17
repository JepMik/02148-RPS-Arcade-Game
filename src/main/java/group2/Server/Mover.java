package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

class Mover implements Runnable {
    Space infoSpace;
    Space spectators;
    Game game;
    ArrayList<String> clients;

    public Mover(Space infoSpace, Space spectators, Game game) {
        this.infoSpace = infoSpace;
        this.spectators = spectators;
        this.game = game;
    }

    public void run() {
        while(true) {
            try {
                //wait until need new player
                infoSpace.get(new ActualField("needPlayer"));
                //wait until there is a player in the queue
                spectators.query(new ActualField("Ready"), new FormalField(String.class));
                //Move in to fill space
                if (game.connectedPlayers() < 2) { // If there is two players in playing space
                    //Move in and
                    Object[] res = infoSpace.get(new ActualField("Spectators"), new FormalField(Object.class));
                	clients = (ArrayList<String>)((ArrayList<String>)res[1]).clone();
					if (clients.size() == 0) {
                    	infoSpace.put("Spectators", clients);
                    	continue;
                    }
                    String user = clients.get(0);
                    System.out.println("Moved user " + user + " from spectators to game");
                    clients.remove(0);
                    infoSpace.put("Broadcast", "Moved", user);
                    spectators.get(new ActualField("Ready"), new FormalField(String.class));
                    spectators.put(user);
                    game.addPlayer(user);

                    infoSpace.put("Spectators", clients);
                }
            } catch (InterruptedException e) {}
        }
    }
}

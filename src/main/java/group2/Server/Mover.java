package group2.Server;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

class Mover implements Runnable {
    Space InfoSpace;
    Space spectators;
    Game game;
    ArrayList<String> clients;

    public Mover(Space InfoSpace, Space spectators, Game game, ArrayList<String> clients) {
        this.InfoSpace = InfoSpace;
        this.spectators = spectators;
        this.game = game;
        this.clients = clients;
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
                    System.out.println("Moved user " + clients.get(0) + " from spectators to game");
                    spectators.get(new ActualField("Ready"), new FormalField(String.class));
                    spectators.put(clients.get(0));
                    game.addPlayer(clients.get(0));
                    clients.remove(0);
                }
            } catch (InterruptedException e) {}
        }
    }
}

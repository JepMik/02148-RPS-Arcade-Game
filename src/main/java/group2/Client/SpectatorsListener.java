package group2.Client;

import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.jspace.ActualField;

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

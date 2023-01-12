package group2.Server;

import java.util.ArrayList;
import java.util.Scanner;

import org.jspace.*;

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

        ArrayList<String> spectatingUsers = new ArrayList<String>();
        ArrayList<String> clients = new ArrayList<String>();
        
        new Thread(new Chat(chat, infoSpace, clients)).start();
        new Thread(new ChatUserUpdater(chat, infoSpace, clients, ping)).start();

        Game game = new Game(playing, infoSpace);
        new Thread(game).start();
        new Thread(new SpectatorUserUpdater(spectators, spectatingUsers)).start();

        new Thread(new Mover(infoSpace, spectators, game, spectatingUsers)).start();


        while(true) {
            //If user to remove
            String user = (String)infoSpace.get(new ActualField("Removed"), new FormalField(String.class))[1];
            chat.getAll(new ActualField("output"), new ActualField(user), new FormalField(String.class));
			game.removePlayer(user);
        }
    }
}



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
        SequentialSpace serverInfo = new SequentialSpace();
        // Sends game info between classes on the server
        SequentialSpace infoSpace = new SequentialSpace();

        // adding to repository
        repository.add("chat", chat);
        repository.add("playing", playing);
        repository.add("spectators", spectators);
        repository.add("ping", ping);
        repository.add("serverInfo", serverInfo);


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
		new Thread(new UserRemover(infoSpace, chat, game)).start();


        while(true) {
            //If user to remove
            Object[] tuple = infoSpace.get(new ActualField("Broadcast"), new FormalField(String.class), new FormalField(Object.class));
			Object[] response = infoSpace.query(new ActualField("Clients"), new FormalField(Object.class));
           	ArrayList<String> allClients = (ArrayList<String>)((ArrayList<String>)response[1]).clone();
           	String player1, player2, score1, score2;
           	switch ((String)tuple[1]) {
				case "MatchStart":
					player1 = ((String[])tuple[2])[0];
					player2 = ((String[])tuple[2])[1];
					for (int i=0; i < allClients.size(); i++){
                        if (allClients.get(i).equals(player2)) {
                        	playing.put("MatchStart", allClients.get(i), player2, player1);
                        } else {
                        	playing.put("MatchStart", allClients.get(i), player1, player2);
                        }
                    }
					break;
				case "Current score":
					for (int i=0; i < allClients.size(); i++){
                        serverInfo.put("Score", allClients.get(i), tuple[2]);
                    }
					break;
				case "Chat message":
					for (int i=0; i < allClients.size(); i++){
                        serverInfo.put("New message", allClients.get(i), tuple[2]);
                    }
					break;
				
            }

        }
    }
}



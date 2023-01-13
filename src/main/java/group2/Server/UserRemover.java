package group2.Server;
import org.jspace.FormalField;
import org.jspace.ActualField;
import org.jspace.Space;

// To remove users from game
public class UserRemover implements Runnable {
	private Space infoSpace;
	private Space chat;
	private Game game;
	public UserRemover(Space infoSpace, Space chat, Game game) {
		this.infoSpace = infoSpace;
		this.chat = chat;
		this.game = game;
	}

	public void run() {
		try {
			String user = (String)infoSpace.get(new ActualField("Removed"), new FormalField(String.class))[1];
            System.out.println("Removeing " + user);
            chat.getAll(new ActualField("output"), new ActualField(user), new FormalField(String.class));
			game.removePlayer(user);
		} catch (InterruptedException e) {}
	}

}

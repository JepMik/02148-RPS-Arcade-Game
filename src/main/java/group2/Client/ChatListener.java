package group2.Client;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.RemoteSpace;

//Listen to chat space for messages from other users
class ChatListener implements Runnable {

    Space chat;
    String username;

    public ChatListener(RemoteSpace chat, String username) {
        this.chat = chat;
        this.username = username;
    }

    public void run() {
        while(true) {
            try{
                Object[] output = chat.get(new ActualField("output"), new ActualField(username), new FormalField(String.class));
                System.out.println((String)output[2]);
            } catch (InterruptedException e) {}
        }
    }

}

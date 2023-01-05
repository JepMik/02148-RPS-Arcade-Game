package group2;

import org.jspace.*;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner input = new Scanner(System.in);
        RemoteSpace chat = new RemoteSpace("tcp://localhost:9001/chat?keep");

        System.out.print("Please enter your username: ");
        String user_name = input.nextLine();
        chat.put(user_name);

        new Thread(new MyScreen(chat, user_name)).start();

        System.out.println("Say something...");
        while(true) {
            String message = input.nextLine();
            chat.put(user_name, message);
        }
    }
}

class MyScreen implements Runnable{

    Space chat;
    String user_name;

    public MyScreen(RemoteSpace chat, String user_name) {
        this.chat = chat;
        this.user_name = user_name;
    }

    public void run() {
        while(true) {
            try{
                Object[] output = chat.get(new ActualField("output"), new ActualField(user_name), new FormalField(String.class));
                System.out.println((String)output[2]);
            } catch (InterruptedException e) {}
        }
    }

}
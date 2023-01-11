//package group2.Client;
//
//import org.jspace.RemoteSpace;
//import org.jspace.Space;
//import org.jspace.SequentialSpace;
//
//
//public class ClientFactory {
//
//    public Client getClient(String ip, String username){
//        RemoteSpace chat = new RemoteSpace("tcp://" + ip + ":9001/chat?keep");
//        RemoteSpace ping = new RemoteSpace("tcp://" + ip + ":9001/ping?keep");
//        RemoteSpace spectators = new RemoteSpace("tcp://" + ip + ":9001/spectators?keep");
//        RemoteSpace playing = new RemoteSpace("tcp://" + ip + ":9001/playing?keep");
//
//        // Space to keep Pong active
//        Space active = new SequentialSpace(1);
//        active.put("active");
//
//        chat.put(username);
//        spectators.put("Joined", username);
//        spectators.put("Ready", username);
//       
//        return client;
//    }
//
//}

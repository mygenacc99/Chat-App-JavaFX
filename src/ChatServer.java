import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ChatServer {

    private int port;
    private Set<String> Users = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private Map<String, Set<String>> Groups = new HashMap<>();
    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(6969);
        server.execute();
    }


    void sendToUser(String message, String user){
        for (UserThread aUser : userThreads) {
            String currName = aUser.getUserName();
            if (currName.equals(user)) {
                aUser.sendMessage(message);
            }
        }
    }

    public void sendToAllUser(String message, UserThread exclude){
        for (UserThread aUser : userThreads) {
            if(aUser != exclude)
                aUser.sendMessage(message);
        }
    }

    void sendToGroup(String message,String excludeUser, String groupName) {
        Set<String> user = Groups.get(groupName);

        user.forEach((String username) -> {
            if (!excludeUser.equals(username)) {
                sendToUser(message, username);
            }
        });

    }

    void addUser(String user){
        this.Users.add(user);
    }

    void caGroup(String name, Set<String> Users) {
        if (!Groups.containsKey(name))
            this.Groups.put(name, Users);
        else{
            Groups.get(name).addAll(Users);
        }
    }


    public Set<String> getUsers() {
        return Users;
    }

    public Map<String, Set<String>> getGroups() {
        return Groups;
    }

    public String getListUserInGroup(String groupName){
        String[] rs = {""};
        if (Groups.containsKey(groupName)){
            Groups.get(groupName).forEach((String user) -> {
                rs[0] = rs[0] + (user + "|");
            });
        }
        return rs[0];
    }

    public void removeUserFromGroup(String groupname, String username){
        Groups.get(groupname).remove(username);
    }

    public void removeUser(String user, UserThread aUser) {
        Users.remove(user);
        userThreads.remove(aUser);
        for(String key: Groups.keySet()){
            Groups.get(key).remove(user);
        }

        System.out.println("The user " + user + " quitted");
    }


}


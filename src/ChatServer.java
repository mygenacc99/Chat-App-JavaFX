import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ChatServer {
//    public static int DEFAULT_PORT = 6969;
//    public static String fileName = "/home/colab/IdeaProjects/Network Programming/src/Exercises/Chuong4/Cau4/accounts.txt";
//    public static Map<String, String> accounts = null;
//
//    public static Map<String, String> getDataFile() throws IOException {
//        Map<String, String> accounts = new HashMap<>();
//
//        BufferedReader bRead = new BufferedReader(new FileReader(fileName));
//        String line = "";
//        while ((line = bRead.readLine())!=null) {
//            if(line.isEmpty()) continue;
//            String splited[] = line.split("\\|");
//            accounts.put(splited[0], splited[1]);
//        }
//        bRead.close();
//        return accounts;
//    }
//
//    public void addAccount(String username, String password) throws IOException {
//        File file = new File(fileName);
//        FileWriter fileWriter = new FileWriter(file, true);
//        fileWriter.write(username+ "|" + password +"\n");
//        synchronized (accounts) {
//            accounts.put(username, password);
//        }
//        fileWriter.close();
//    }
//
//    public String requestProcessing(String request) throws IOException {
//        String reply = "";
//        String[] slipted = request.split("\\|");
//        System.out.println(slipted.length);
//        // Check login.
//        synchronized (accounts){
//            if (slipted[0].equals("1")) {
//                if (accounts.get(slipted[1]) != null) {
//                    if (accounts.get(slipted[1]).equals(slipted[2])) reply = "Login successfully!";
//                    else reply = "The username or password is wrong! Please check it again!";
//                } else reply = "The username or password is wrong! Please check it again!";
//            } else { // Register
//                if (accounts.containsKey(slipted[1])) {
//                    reply = "The username already exists. Please use a different username!";
//                } else {
//                    addAccount(slipted[1], slipted[2]);
//                    reply = "Register successfully!";
//                }
//            }
//        }
//        return reply;
//    }
//
//    public void loginExecute() throws IOException {
//
//        ServerSocket ss = new ServerSocket(DEFAULT_PORT);
//
//        while (true) {
//            System.out.println("Server is waiting to connect...");
//            Socket socket = ss.accept();
//
//            ProcessingThread pt = new ProcessingThread(socket, this);
//            pt.start();
//        }
//    }
//
//


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


    void senToUser(String message, String user){
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
                senToUser(message, username);
            }
        });

    }

    void addUser(String user){
        this.Users.add(user);
    }

    void addGroup(String name, Set<String> Users) {
        this.Groups.put(name, Users);
    }

    void addUserToGroup(String name, String user){
        if (Groups.containsKey(name)){
            Groups.get(name).add(user);
        }

    }

    public Set<String> getUsers() {
        return Users;
    }

    public Map<String, Set<String>> getGroups() {
        return Groups;
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




    boolean hasUsers() {
        return !this.Users.isEmpty();
    }
}

//class ProcessingThread extends Thread{
//    Socket socket;
//    ChatServer server;
//
//    ProcessingThread(Socket socket, ChatServer serverSocket){
//        this.socket = socket;
//        this.server = serverSocket;
//    }
//
//    @Override
//    public void run() {
//        InputStream is = null;
//        OutputStream os = null;
//        try {
//            os = socket.getOutputStream();
//            is = socket.getInputStream();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            PrintWriter writer = new PrintWriter(os, true);
//            String request = reader.readLine();
//            String reply = server.requestProcessing(request);
//            writer.println(reply);
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}

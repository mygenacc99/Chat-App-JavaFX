import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    private String userName;
    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            /*
                Login.
             */
            String uName = reader.readLine();
            this.userName = uName;
            System.out.println(userName + " logins.");
            /*
                Send list of online user to new user.
                And save the new user
             */
            String serverMessage = "";
            for(String user:server.getUsers()){
                serverMessage += user + "|";
            }
            server.sendToUser(serverMessage, userName);
            server.sendToAllUser("*newuser"+userName, this);
            server.addUser(userName);


            String clientCommand;
            String splited[];
            do {
                clientCommand = reader.readLine();
                System.out.println("Client sended:" + clientCommand);
                splited = clientCommand.split("\\|");


                if(clientCommand.equals("bye")){
                    server.sendToAllUser("*userquit|"+userName, this);
                    break;
                }

                if(clientCommand.startsWith("*quitgroup")){
                    server.sendToGroup("*sendgroup|"+splited[1]+"|"+userName+ " has quitted!",userName, splited[1]);
                    server.removeUserFromGroup(splited[1], userName);
                    continue;
                }


                if(clientCommand.startsWith("*newgroup")){
                    Set<String> listMember = new HashSet<>();
                    for(int i = 2; i<splited.length; i++){
                        listMember.add(splited[i]);
                    }
                    server.CustomizeGroup(splited[1], listMember);
                    server.sendToGroup("*newgroup|" + splited[1] +"|" + server.getListUserInGroup(splited[1]), userName, splited[1]);

                    continue;
                }

                if(clientCommand.startsWith("*sendgroup")){
                    server.sendToGroup(clientCommand, userName, splited[1] );

                    continue;
                }

                if(clientCommand.startsWith("+groupmem")){

                }

                if (clientCommand.startsWith("*luig")){ // *luig|groupname
                    String list = server.getListUserInGroup(splited[1]);
                    writer.println("*luig|" + splited[1] +"|"+ list);
                }

                // splited[0]: receiver, [1]: message
                if(splited.length < 2) continue;
                serverMessage = uName + "|" + splited[1]; // Format: [name]: message
                server.sendToUser(serverMessage, splited[0]);

            } while (true);

            server.removeUser(uName, this);
            socket.close();

//            serverMessage = uName + " has quitted.";
//            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUsers());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }

    /*
        command has format like: *newgroup|group's name| user's name 1| user's name 2|...
     */
    public void addGroup(String command){
        String splited[] = command.split("\\|");
        String grroupName = splited[1];



    }
}


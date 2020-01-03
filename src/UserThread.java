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

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String serverMessage = "";
            String clientCommand;
            String splited[];
            do {
                clientCommand = reader.readLine();
                System.out.println("Client sended:" + clientCommand);
                splited = clientCommand.split("\\|");

                if(clientCommand.startsWith("*userLogin")){
                    this.userName = splited[1];
                    if (server.getUsers().contains(userName)){ // Check exist.
                        sendMessage("*loginfail");
                        break;
                    }

                    System.out.println(userName + " logins.");

                    for(String user:server.getUsers()){  // Get all users on server.
                        serverMessage += user + "|";
                    }
                    server.sendToUser("*listUser" + "|"+ serverMessage, userName);
                    server.sendToAllUser("*newuser"+userName, this);
                    server.addUser(userName);
                }

                if(clientCommand.equals("*bye")){
                    server.sendToAllUser("*userquit|"+userName, this);
                    server.removeUser(userName, this);
                    break;
                }

                if(clientCommand.startsWith("*quitgroup")){
                    server.sendToGroup("*sendgroup|"+splited[1]+"|"+userName+ " has quit!",userName, splited[1]);
                    server.sendToGroup("*quitgroup|"+splited[1]+"|"+userName, userName, splited[1]);
                    server.removeUserFromGroup(splited[1], userName);
                    continue;
                }


                if(clientCommand.startsWith("*cagroup")){
                    Set<String> listMember = new HashSet<>();
                    for(int i = 2; i<splited.length; i++){
                        listMember.add(splited[i]);
                    }
                    server.caGroup(splited[1], listMember);
                    server.sendToGroup("*cagroup|" + splited[1] +"|" + server.getListUserInGroup(splited[1]), userName, splited[1]);

                    continue;
                }

                if(clientCommand.startsWith("*sendgroup")){
                    server.sendToGroup(clientCommand, userName, splited[1] );

                    continue;
                }

                if(clientCommand.startsWith("*sendUser")){
                    if(splited.length < 3) continue;
                    serverMessage = "*sendUser"+"|"+userName + "|" + splited[2]; // Format: [name]: message
                    server.sendToUser(serverMessage, splited[1]);
                    continue;
                }

            } while (true);

            socket.close();

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }

}


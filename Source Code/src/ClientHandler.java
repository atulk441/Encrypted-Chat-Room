import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalTime;
import java.util.Date;

public class ClientHandler implements Runnable{
    //DATA ITEMS
    private BufferedReader bReader;
    private BufferedWriter bWriter;
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();//list keeping track of all clients in the server
    private Socket socket;
    private String clientUser;
    Date date = new Date();
    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String dt = dateTime.format(dateTimeFormat);
    String name;
     static String names="";
    //Constructor
    public ClientHandler(Socket socket, String name) {
        try {
            this.socket=socket;
            this.name = name;

            bWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientUser=bReader.readLine();
            clientHandlers.add(this);
            setNames(name);
            broadcast("SERVER: \""+clientUser+"\" has entered the chat at <"+ dt+">");
        }catch(IOException e) {
            closeAll(socket,bReader,bWriter);
        }
    }
    public void broadcast1(String names, String name, int flag) {//to send the message from one client to all the other clients

        for(ClientHandler clientHandler:clientHandlers) {
            try {
                if(clientHandler.clientUser.equals(name)) {
                    clientHandler.bWriter.write("WELCOME: ");
                    clientHandler.bWriter.write(names);
                    if(!names.equals("You are alone here.") && flag==0){
                        clientHandler.bWriter.write(" is present in this chat server.");
                    }
                    if(!names.equals("You are alone here.") && flag!=0){
                        clientHandler.bWriter.write(" are present in this chat server.");
                    }

                    clientHandler.bWriter.newLine();
                    clientHandler.bWriter.flush();
                }
            }catch(IOException e) {
                closeAll(socket,bReader,bWriter);
            }
        }
    }
public void setNames(String name) throws IOException{
        String finMessage = "";
        if(names.equals("")){
            broadcast1("You are alone here.", name, 0);
        }
        else {
            String newS = "";
            for (int i = 0; i < names.length() - 2; i++) {
                newS += names.charAt(i);
            }
            int countCommas=0;
            int commaIndex=0;
            for(int i=0;i<newS.length();i++){
                if(newS.charAt(i)==','){
                    countCommas+=1;
                    commaIndex=i;
                }
            }
            if(commaIndex!=0) {
                finMessage = newS.substring(0, commaIndex) + " and" + newS.substring(commaIndex + 1);
                broadcast1(finMessage, name, commaIndex);
            }
            else{
                broadcast1(newS, name, commaIndex);
            }
        }
            names+= "\"";
            names += name;
            names+="\"";
            names +=", ";


}
    public void removeClientHandler() {
        clientHandlers.remove(this);
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dt = dateTime.format(dateTimeFormat);
        broadcast("SERVER: \""+clientUser+"\" has left the chat room at <" + dt +">");
        System.out.println("\""+clientUser+ "\" left the chat room at <" + dt +">");
    }

    public void broadcast(String message) {//to send the message from one client to all the other clients
        for(ClientHandler clientHandler:clientHandlers) {
            try {
                if(!clientHandler.clientUser.equals(clientUser)) {
                    clientHandler.bWriter.write(message);
                    clientHandler.bWriter.newLine();
                    clientHandler.bWriter.flush();
                }
            }catch(IOException e) {
                closeAll(socket,bReader,bWriter);
            }
        }
    }

    public void closeAll(Socket socket, BufferedReader read, BufferedWriter write) {
        removeClientHandler();
        try {
            if (read != null) {
                read.close();
            }
            if (write != null) {
                write.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String clientMessage;

        while(socket.isConnected()) {
            try {
                clientMessage=bReader.readLine();
                broadcast(clientMessage);
            }catch(IOException e) {
                closeAll(socket, bReader,bWriter);
                break;
            }
        }
    }
}

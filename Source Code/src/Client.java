import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    //DATA ITEMS
    private Socket socket;
    private BufferedReader bReader;
    private BufferedWriter bWriter;
    private String username;
     String name;
    Scanner input = new Scanner(System.in);

    public Client(Socket socket, String username) {//Constructor
        try {
            this.socket = socket;
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bReader, bWriter);
        }

    }
    public Client(String name){
        this.name = name;
    }



    public void listen() {//method to receive message
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                String decrypt="";
                int key = 238;

                char a;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bReader.readLine();
                        if(msgFromGroupChat.contains("SERVER: ")){
                            System.out.println("***************************   "+msgFromGroupChat+ "   ***************************");
                        }
                        else if (msgFromGroupChat.contains("WELCOME: ")){
                            System.out.println("*****************************************   "+msgFromGroupChat+ "   *****************************************");
                        }

                        else{
                            int count=0;

                            for(int i = 0; i < msgFromGroupChat.length();i++){
                                if(msgFromGroupChat.charAt(i)!=':' || msgFromGroupChat.charAt(i+1)!=' '){
                                    count++;
                                }
                                else{
                                    count++;
                                    break;
                                }
                            }
                            count++;
                            for(int i = 0; i <count;i++){
                                System.out.print(msgFromGroupChat.charAt(i));
                            }


                            for(int i = count; i<msgFromGroupChat.length();i++){
                                a=(char)(msgFromGroupChat.charAt(i)^key);
                                decrypt+=a;
                        }
                        msgFromGroupChat = decrypt;
                        System.out.println(msgFromGroupChat);
                        decrypt="";

                        }

                    } catch (IOException e) {
                        closeAll(socket, bReader, bWriter);
                    }
                }
            }
        })
                .start();
    }

    public void send() {//method to send a Client's message
        try {
            bWriter.write(username);
            bWriter.newLine();
            bWriter.flush();

            Scanner scanner = new Scanner(System.in);

            char b;
            int key = 238;
            String encrypt="";
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                for(int i = 0;i < message.length();i++){
                    b=(char)(message.charAt(i)^key);
                    encrypt+=b;
                }
                message=encrypt;
                bWriter.write(username + ": " + message);
                encrypt="";
                bWriter.newLine();

                bWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bReader, bWriter);
        }
    }

    public void closeAll(Socket socket, BufferedReader read, BufferedWriter write) {
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

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dt = dateTime.format(dateTimeFormat);
        Socket socket = new Socket("localhost", 1234);
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        dout.writeUTF(username);
        dout.writeUTF(dt);
        Client client = new Client(socket, username);
        client.listen();
        client.send();
    }
}

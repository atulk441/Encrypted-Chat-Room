import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //DATA ITEMS
    private ServerSocket serverSocket;
    private String name="";
    public Server(ServerSocket serverSocket) {//Constructor
        this.serverSocket = serverSocket;

    }


    public void closeServer() throws IOException{//close the server
            if(serverSocket!=null) {
                serverSocket.close();
            }

    }

    public void startServer() throws IOException{//method to start the server
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String clientName = (String)(dis.readUTF());
                String dateTime = (String)(dis.readUTF());
                System.out.println("\""+clientName+ "\" entered in the chat room at <" + dateTime +">");
                name = clientName;
                ClientHandler clientHandler=new ClientHandler(socket, name);//create a clienthandler to manage clients
                Thread thread = new Thread(clientHandler);
                thread.start();

                name+=", ";
            }

    }

    public static void main(String[] args) throws IOException {//main class

        ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();

    }

}

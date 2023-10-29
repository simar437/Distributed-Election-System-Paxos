import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class CommunicationServer {

    // get id from string
    public static ConcurrentHashMap<String, ClientHandlerWriter> writer = new ConcurrentHashMap<>();


    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(4567);
        System.out.println("Server running...");
        // Start server
        while (true) {
            Socket s = ss.accept();
            new Thread(new ClientHandler(s)).start();
        }
    }
}

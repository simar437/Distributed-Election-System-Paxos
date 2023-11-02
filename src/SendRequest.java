import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class SendRequest {

    Socket socket;
    BufferedReader reader;
    PrintWriter writer;


    /**
     * Constructor for SendRequest
     * Initializes the reader and writer
     *
     * @param s The socket to be used for sending and receiving
     * @throws IOException
     */
    SendRequest(Socket s) throws IOException {
        socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Constructor for SendRequest that takes in a host and port
     *
     * @param host The host to be used for sending and receiving
     * @param port The port to be used for sending and receiving
     * @throws IOException If the socket cannot be created
     */
    SendRequest(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    boolean isClosed() {
        return !socket.isConnected() || socket.isClosed();
    }

    /**
     * Gets the headers from a string
     *
     * @param str The string to be parsed
     * @return The headers in the string
     */
    static HashMap<String, String> getHeaders(String str) {
        HashMap<String, String> headers = new HashMap<>();
        String[] header = str.split("\n");
        for (String s : header) {
            try {
                String[] h = s.split(": ");
                if (h.length >= 2) {
                    headers.put(h[0], h[1]);
                }
            } catch (Exception e) {
                System.out.println("String: " + str);
                System.out.println("S: " + s);
                e.printStackTrace();
            }
        }
        return headers;
    }

    /**
     * Sends the request
     *
     * @param request The request to be sent
     */
    void send(String request) {
        if (isClosed() || request == null || request.isEmpty()) {
            return;
        }
        try {
            writer.println(request);
            writer.println("---END---");
        } catch (Exception ignore) {}
    }

    /**
     * Receives the response
     *
     * @return The response received
     */
    String receive() {
        if (isClosed()) {
            return null;
        }
        StringBuilder response = new StringBuilder();

        try {
            // Read the response line by line
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty() && !line.equals("---END---")) {
                response.append(line).append("\n");
            }
        } catch (Exception ignore) {
            return "CONNECTION CLOSED";
        }
        if (response.length() == 0) {
            return null;
        }
        return response.toString();
    }



    /**
     * Sends a sync message to the server
     *
     * @param id The request to be sent
     */
    void sendSyncMessage(String id) {
        String message = "TO: SERVER\n" +
                "FROM: " + id + "\n" +
                "MESSAGE: Connect";
        send(message);
    }

}

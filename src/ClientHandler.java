import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Client Handler for the Communication Server
 * It splits the input and output streams of the socket
 */
public class ClientHandler implements Runnable {


    SendRequest sr;

    public ClientHandler(Socket s) throws IOException {
        sr = new SendRequest(s);
    }

    @Override
    public void run() {
        try {
            String id = SendRequest.getHeaders(sr.receive()).get("FROM");
            new Thread(new ClientHandlerReader(sr, id)).start();
            ClientHandlerWriter cw = new ClientHandlerWriter(sr, id);
            CommunicationServer.writer.put(id, cw);
            new Thread(cw).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * The Client Handler Reader for the Communication Server
 * It reads the messages from the socket
 */
class ClientHandlerReader implements Runnable {

    SendRequest sr;
    String id;
    public ClientHandlerReader(SendRequest sr, String id) throws IOException {
        this.sr = sr;
        this.id = id;
    }

    /**
     * Reads the messages from the socket, if no message is received, it waits for 100ms and then checks again
     */
    void readAlways() {
        while (true) {
            try {
                String message = sr.receive();
                if (message == null || message.equals("")) {
                    synchronized (this) {
                        wait(100);
                    }
                    continue;
                }
                if (message.equals("CONNECTION CLOSED")) {
                    CommunicationServer.writer.get(id).messages.add(message);
                    CommunicationServer.writer.remove(id);
                    break;
                }

                HashMap<String, String> headers = SendRequest.getHeaders(message);
                if (!headers.containsKey("TO")) {
                    continue;
                }
                String to = headers.get("TO");
                if (to.equals("EVERYONE")) {
                    for (Map.Entry<String, ClientHandlerWriter> c : CommunicationServer.writer.entrySet()) {
                        if (c.getKey().equals(id)) {
                            continue;
                        }
                        c.getValue().addAndNotify(message);
                    }
                } else {
                    if (!CommunicationServer.writer.containsKey(to)) {
                        continue;
                    }
                    ClientHandlerWriter writer = CommunicationServer.writer.get(to);
                    writer.addAndNotify(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        readAlways();
    }
}

/**
 * The Client Handler Writer for the Communication Server
 * It writes the messages to the socket
 */
class ClientHandlerWriter implements Runnable {

    SendRequest sr;
    String id;
    public final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

    public ClientHandlerWriter(SendRequest sr, String id) {
        this.sr = sr;
        this.id = id;
    }

    /**
     * Adds the message to the queue and notifies the thread
     *
     * @param message The message to be added to the queue
     */
    public synchronized void addAndNotify(String message) {
        messages.add(message);
        this.notify();
    }

    /**
     * Writes the messages to the socket, if no message is present in the queue, it waits for the thread to notify
     */
    void writeAndWait() {
        try {
            while (true) {
                synchronized (this) {
                    while (!messages.isEmpty()) {
                        String message = messages.peek();
                        messages.remove();
                        if (message.equals("CONNECTION CLOSED")) {
                            return;
                        }
                        sr.send(message);
                    }
                    wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        writeAndWait();
    }
}
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Acceptor {
    boolean isAccepted = false;
    String acceptedID = "";
    String acceptedValue = "";
    public AtomicInteger maxNo = new AtomicInteger(0);

    String id;

    SendRequest sr;


    Acceptor(SendRequest sr, String id) {
        this.sr = sr;
        this.id = id;
    }

    /**
     * Follows the acceptor algorithm, when a prepare request is received
     *
     * @param str The HTTP message
     */

    void prepare(String str) {
        HashMap<String, String> headers = SendRequest.getHeaders(str);
        System.out.println("Received prepare request from " + headers.get("FROM") + " with prepare no " + headers.get("PREPARE_NO"));
        String prepareNo = headers.get("PREPARE_NO");
        if (Integer.parseInt(prepareNo) > maxNo.get()) {
            if (isAccepted) {
                System.out.println("Sending Already Accepted Value to " + headers.get("FROM") + " with accepted value " + acceptedValue);
                String promise = "TO: " + headers.get("FROM") + "\n" +
                        "FROM: " + id + "\n" +
                        "PROMISE_NO: " + prepareNo + "\n" +
                        "ACCEPTED_ID: " + acceptedID + "\n" +
                        "ACCEPTED_VALUE: " + acceptedValue;
                sr.send(promise);
            } else {
                System.out.println("Sending Promise to " + headers.get("FROM") + " with prepare no " + prepareNo);
                maxNo.set(Integer.parseInt(prepareNo));
                String promise = "TO: " + headers.get("FROM") + "\n" +
                        "FROM: " + id + "\n" +
                        "PROMISE_NO: " + prepareNo;
                sr.send(promise);
            }
        }
    }

    /**
     * Follows the acceptor algorithm, when a proposal is received
     *
     * @param str The HTTP message
     */
    void accept(String str) {
        HashMap<String, String> headers = SendRequest.getHeaders(str);
        String proposeNo = headers.get("PROPOSE_NO");
        System.out.println("Received accept request from " + headers.get("FROM") + " with propose no " + proposeNo);
        if (Integer.parseInt(proposeNo) == maxNo.get()) {
            System.out.println("Sending Accept to " + headers.get("FROM") + " with propose no " + proposeNo);
            isAccepted = true;
            acceptedID = proposeNo;
            acceptedValue = headers.get("VALUE");
            String accept = "TO: " + headers.get("FROM") + "\n" +
                    "FROM: " + id + "\n" +
                    "ACCEPT: " + acceptedID + "\n" +
                    "ACCEPTED_VALUE: " + acceptedValue;
            sr.send(accept);
            System.out.println(acceptedValue + " has been elected as the President.");
            System.out.println("Press Ctrl+C to exit.");
        }
    }
    public static void main(String[] args) throws IOException {
        SendRequest sr = new SendRequest("localhost", 4567);
        String id = args[0];
        System.out.println("Acceptor " + id + " started.");
        sr.sendSyncMessage(id);



        Acceptor acc = new Acceptor(sr, id);

        while (true) {
            String str = sr.receive();
            if (str == null || str.isEmpty()) {
                continue;
            }
            if (str.contains("PREPARE_NO")) {
                acc.prepare(str);
            }
            if (!acc.isAccepted) {
                if (str.contains("ACCEPTED_ID")) {
                    acc.isAccepted = true;
                    acc.acceptedValue = SendRequest.getHeaders(str).get("ACCEPTED_VALUE");
                    acc.acceptedID = SendRequest.getHeaders(str).get("ACCEPTED_ID");
                }
                if (str.contains("PROPOSE_NO")) {
                    acc.accept(str);
                }

                if (acc.isAccepted) {
                    System.out.println(acc.acceptedValue + " has been elected as the President.");
                    System.out.println("Press Ctrl+C to exit.");
                }

            }
        }
    }

}

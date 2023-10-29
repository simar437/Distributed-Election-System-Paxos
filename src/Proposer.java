import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Non-blocking asynchronous reader for the Communication Server
 */
class Reader implements Runnable {
    SendRequest sr;
    Acceptor acc;

    Reader(SendRequest sr, Acceptor acc) {
        this.sr = sr;
        this.acc = acc;
    }

    @Override
    public void run() {
        while (true) {
            String str = null;
            try {
                str = sr.receive();
                if (str == null) {
                    continue;
                }
                HashMap<String, String> headers = SendRequest.getHeaders(str);
                if (str.contains("PROMISE")) {
                    if (str.contains("PROMISE_NO")) {
                        String promiseNo = headers.get("PROMISE_NO");
                        if (str.contains("ACCEPTED")) {
                            acc.isAccepted = true;
                            acc.acceptedValue = headers.get("ACCEPTED_VALUE");
                            acc.acceptedID = headers.get("ACCEPTED_ID");

                            String msg = "TO: EVERYONE\n" +
                                    "FROM: " + acc.id + "\n" +
                                    str.substring(str.indexOf("PROMISE_NO"));
                            sr.send(msg);
                            return;
                        }
                        if (Proposer.noOfPromises.containsKey(promiseNo)) {
                            Proposer.noOfPromises.put(promiseNo, Proposer.noOfPromises.get(promiseNo) + 1);
                        } else {
                            Proposer.noOfPromises.put(promiseNo, 1);
                        }
                    }
                }
                if (str.contains("ACCEPT:")) {
                    String acceptedNo = headers.get("ACCEPT");
                    if (Proposer.noOfAccepts.containsKey(acceptedNo)) {
                        Proposer.noOfAccepts.put(acceptedNo, Proposer.noOfAccepts.get(acceptedNo) + 1);
                    } else {
                        Proposer.noOfAccepts.put(acceptedNo, 1);
                    }
                }
                else if (str.contains("PREPARE_NO")) {
                    acc.prepare(str);
                }
                else if (str.contains("PROPOSE_NO")) {
                    acc.accept(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


public class Proposer {
    static ConcurrentHashMap<String, Integer> noOfPromises = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Integer> noOfAccepts = new ConcurrentHashMap<>();

    /**
     * Generates a number using the event number and the node number
     * Uses the formula L(e) = M * Li(e) + i
     * where M is the number of nodes, e is the event number and i is the node number
     * M = 3, as there are 3 proposers
     * @param eventNo The event number
     * @param nodeNo  The node number
     * @return The generated number
     */
    static int generator(int eventNo, int nodeNo) {
        final int M = 3;
        return M * eventNo + nodeNo;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        SendRequest sr = new SendRequest("localhost", 4567);
        final String id = args[0];
        final String value = args[0];
        final int nodeNo = Integer.parseInt(args[1]);
        int DELAY = 2500;
        if (args.length == 3) {
            DELAY += Integer.parseInt(args[2]);
        }
        sr.sendSyncMessage(id);
        Acceptor acc = new Acceptor(sr, id);
        Thread t = new Thread(new Reader(sr, acc));
        t.start();
        int prepareNo = 1;
        while (!acc.isAccepted) {
            int gen = generator(prepareNo++, nodeNo);
            String prepNum = String.valueOf(gen);
            if (acc.maxNo.get() >= gen) {
                continue;
            }
            acc.maxNo.set(gen);
            String prepare = "TO: EVERYONE\n" +
                    "FROM: " + id + "\n" +
                    "PREPARE_NO: " + prepNum;
            System.out.println("Sending Prepare to Everyone with Prepare No: " + prepNum + " and Value: " + value);
            sr.send(prepare);
            Thread.sleep(DELAY);
            if (noOfPromises.containsKey(prepNum)) {
                noOfPromises.put(prepNum, noOfPromises.get(prepNum) + (acc.maxNo.get() == gen ? 1 : 0));
                if (noOfPromises.get(prepNum) >= 5) {
                    String propose = "TO: EVERYONE\n" +
                            "FROM: " + id + "\n" +
                            "PROPOSE_NO: " + prepNum + "\n" +
                            "VALUE: " + value;
                    System.out.println("Sending Propose to Everyone with Propose No: " + prepNum + " and Value: " + value);
                    sr.send(propose);
                    Thread.sleep(DELAY);
                    if (noOfAccepts.containsKey(prepNum)) {
                        noOfAccepts.put(prepNum, noOfAccepts.get(prepNum) + (acc.maxNo.get() == gen ? 1 : 0));
                        if (noOfAccepts.get(prepNum) >= 5) {
                            acc.isAccepted = true;
                            acc.acceptedValue = value;
                            acc.acceptedID = prepNum;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println(acc.acceptedValue + " has been elected as the President.");
        System.out.println("Press Ctrl+C to exit.");
        System.in.read();

    }
}

# Election System using Paxos Algorithm

## Build and Run
To build the project, run the following command:
```
make
```
Run the communication server using:
```
make server
```
The communication server will be running on port 4567.
Run any member using:
```
make M<NUMBER>
```
for acceptors, additional argument can be given:
```
make M<NUMBER> DELAY=<DELAY>
```
where `<DELAY>` is the delay in milliseconds.

Additionally, to manually run the Proposers, run the following command:
```
make proposer NAME=<M1> UNIQUE_ID=<UNIQUE_ID>
```
where `<M1>` is the name of the proposer and provide a unique increasing id for each proposer.

To run the Acceptor, manually, run the following command:
```
make acceptor NAME=<M4>
```
where `<M4>` is the name of the acceptor

Run Test cases using:
```
make test
```

## Program Specifications
### CommunicationServer, ClientHandler Class
The communication server is a multithreaded server that listens to all the incoming messages from
the proposers and acceptors. It is responsible for sending the messages to the appropriate destination.
It follows the following syntax:
```
TO: <DESTINATION>
FROM: <SOURCE>
...
```
### Acceptor Class
The acceptor class implements the acceptor role in the Paxos algorithm. It is responsible for receiving the prepare
and accept messages from the proposers and sending the promise and accepted messages to the proposers. It also
maintains the state of the acceptor and logs the messages received and sent by the acceptor.

### Proposer Class
The proposer class implements the proposer role in the Paxos algorithm. It is responsible for sending the prepare
and accept messages to the acceptors and receiving the promise and accepted messages from the acceptors. It also
maintains the state of the proposer and logs the messages received and sent by the proposer.
The proposer class also implements everything that the Acceptor class does.

### SendRequest Class
Internal class for sending and receiving messages from the communication server and the members.

```Note: All members must be terminated manually using Ctrl+C (NOT IN TEST)``` 

## Testing
### Test 1
One proposer, four acceptors
### Test 2
Three acceptors, two proposers, M1 and M3 having a delay of 5000ms
### Test 3
Two proposers and all acceptors, M2 running with a delay of 5000ms
### Test 4
One proposer, one acceptor
### Test 5
All proposers and acceptors, No delay (will take longer to run), Anyone could win
### Test 6
All acceptors running, M1 running and killed before accept phase. M2 started afterward.
### Test 7
All acceptors running, M1 running and killed after accept phase. M2 started afterward. M1 should win.
### Test 8
All proposers and acceptors, M1 with a delay of 3000ms, M2 with a delay of 2000ms, M3 with a delay of 1000ms.
### Test 9
Only proposers running.
### Test 10
Only acceptors running.

Note: Using test_runner.sh is runnning all the tests.
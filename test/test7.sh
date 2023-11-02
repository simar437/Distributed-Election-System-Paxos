echo "Waiting for all processes to finish..."
exec 3>&1 4>&2 # Save the current state of stdout and stderr
exec > script_log.txt 2>&1

make server &
pid_server=$!
m1=$(make M1 &) &
pid_m1=$!

pid_acceptors=()
for i in {4..9} ; do
    make "M$i" &
    pid_acceptors+=($!)
done
sleep 6
kill -15 $pid_m1

make M2 &
pid_m2=$!
sleep 2

for i in {4..9} ; do
    kill -15 "${pid_acceptors[$i-4]}"
done

kill -15 $pid_server

exec 1>&3 2>&4

m2=$(cat script_log.txt)


if [[ $m2 == *"Sending Already Accepted Value to M2 with accepted value M1"* ]]; then
    echo "Test 7 passed"
else
    echo "Test 7 failed"
    cat script_log.txt
fi
rm script_log.txt
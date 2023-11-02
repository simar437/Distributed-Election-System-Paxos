echo "Waiting for all processes to finish..."
exec 3>&1 4>&2 # Save the current state of stdout and stderr
exec > script_log.txt 2>&1

make server &
pid_server=$!
sleep 1
m1=$(make M1 &) &
pid_m1=$!

pid_acceptors=()
for i in {4..9} ; do
    make "M$i" &
    pid_acceptors+=($!)
done

kill -15 $pid_m1

make M2 &
pid_m2=$!
sleep 10

for i in {4..9} ; do
    kill -15 "${pid_acceptors[$i-4]}"
done

kill -15 $pid_server
kill -15 $pid_m2

exec 1>&3 2>&4

m2=$(cat script_log.txt)


if [[ $m2 == *"M2 has been elected as the President."* ]]; then
    echo "Test Passed"
fi
rm script_log.txt
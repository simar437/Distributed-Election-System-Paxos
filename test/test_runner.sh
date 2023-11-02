#!/bin/bash

INVERT_MATCH=0

# Check if the -v or --invert flag is provided
if [[ "$1" == "-v" ]]; then
    INVERT_MATCH=1
    shift
fi

SLEEP_TIME=10
if [[ "$1" == "-s" ]]; then
    SLEEP_TIME="$2"
    shift
    shift
fi

check_output() {
    local target=$1
    local file=$2
    if [[ $INVERT_MATCH -eq 1 ]]; then
        if grep -q "$target" "$file"; then
            echo "The output in $file contained the target string, which it shouldn't."
            exit 1
        fi
    else
        if ! grep -q "$target" "$file"; then
            echo "The output in $file did not contain the target string."
            exit 1
        fi
    fi
}

target_string="$1"
shift
test_cases=("$@")

pids=() # Store process IDs

for test_case in "${test_cases[@]}"; do
    IFS=":" read -ra parts <<<"$test_case"
    cmd="${parts[0]}"
    outfile="${parts[1]}"
    touch "$outfile"

    make $cmd &>$outfile 2>&1 &
    pids+=($!) # Store the PID
    sleep 1
done

echo "Waiting for all processes to finish..."
sleep "$SLEEP_TIME"

# Kill all started processes
kill -15 "${pids[@]}" >/dev/null 2>&1

sleep 1

# Check outputs for the target string
for test_case in "${test_cases[@]}"; do
    IFS=":" read -ra parts <<<"$test_case"
    cmd="${parts[0]}"
    outfile="${parts[1]}"

    # Skip checking for the server
    if [ "$cmd" != "server" ]; then
        check_output "$target_string" "$outfile"
    fi
    rm "$outfile"
done

echo "Test Passed!"
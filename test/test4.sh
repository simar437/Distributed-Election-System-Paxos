#!/bin/bash

# As only two members are available, no majority can be formed

declare -a test_cases=(
    "server:output_server.txt"
    "M1:output_m1.txt"
    "M4:output_m4.txt"
)

TARGET_STRING="has been elected as the President."

# Run the test runner with the -v flag to invert the match
bash test/test_runner.sh -v "$TARGET_STRING" "${test_cases[@]}"

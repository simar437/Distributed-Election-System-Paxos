#!/bin/bash

# 3 Proposers, 2 Acceptors, with M2 having normal delay and M1, M3 having an additional delay of 5 seconds

declare -a test_cases=(
    "server:output_server.txt"
    "M1 DELAY=5000:output_m1.txt"
    "M2:output_m2.txt"
    "M3 DELAY=5000:output_m3.txt"
    "M4:output_m4.txt"
    "M5:output_m5.txt"
)

TARGET_STRING="M2 has been elected as the President."

bash test/test_runner.sh "$TARGET_STRING" "${test_cases[@]}"

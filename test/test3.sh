#!/bin/bash

# All proposers and acceptors, M1 has a delay of 1 second to prevent Contention, see test 5 also

declare -a test_cases=(
    "server:output_server.txt"
    "M1 DELAY=1000:output_m1.txt"
    "M2:output_m2.txt"
    "M3:output_m3.txt"
    "M4:output_m4.txt"
    "M5:output_m5.txt"
    "M6:output_m6.txt"
    "M7:output_m7.txt"
    "M8:output_m8.txt"
    "M9:output_m9.txt"

)

TARGET_STRING="M3 has been elected as the President."

bash test/test_runner.sh "$TARGET_STRING" "${test_cases[@]}"

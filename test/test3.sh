#!/bin/bash

# Two proposers and  all acceptors, M2 has a delay of 5 second to prevent Contention, see test 5 also

declare -a test_cases=(
    "server:output_server.txt"

    "M4:output_m4.txt"
    "M5:output_m5.txt"
    "M6:output_m6.txt"
    "M7:output_m7.txt"
    "M8:output_m8.txt"
    "M9:output_m9.txt"
    "M1:output_m1.txt"
    "M2 DELAY=5000:output_m2.txt"

)

TARGET_STRING="M1 has been elected as the President."

bash test/test_runner.sh -s 20 "$TARGET_STRING" "${test_cases[@]}"

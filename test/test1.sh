#!/bin/bash

declare -a test_cases=(
    "server:output_server.txt"
    "M4:output_m4.txt"
    "M5:output_m5.txt"
    "M6:output_m6.txt"
    "M7:output_m7.txt"
    "M1:output_m1.txt"
)

TARGET_STRING="M1 has been elected as the President."

bash test/test_runner.sh "$TARGET_STRING" "${test_cases[@]}"

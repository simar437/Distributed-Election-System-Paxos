#!/bin/bash

declare -a test_cases=(
    "server:output_server.txt"

    "M4:output_m4.txt"
    "M5:output_m5.txt"
    "M6:output_m6.txt"
    "M7:output_m7.txt"
    "M8:output_m8.txt"
    "M9:output_m9.txt"
    "M1 DELAY=3000:output_m1.txt"
    "M2 DELAY=2000:output_m2.txt"
    "M3:output_m3.txt"
)

TARGET_STRING="M3 has been elected as the President."

bash test/test_runner.sh "$TARGET_STRING" "${test_cases[@]}"

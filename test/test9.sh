#!/bin/bash

declare -a test_cases=(
    "server:output_server.txt"
    "M1:output_m1.txt"
    "M2:output_m2.txt"
    "M3:output_m3.txt"
)

TARGET_STRING="has been elected as the President."

bash test/test_runner.sh -v "$TARGET_STRING" "${test_cases[@]}"

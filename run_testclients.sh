#!/bin/sh

NUM_NUMBERS=10000
if [ "$1" != "" ]; then
    NUM_NUMBERS=$1
fi

echo "Sending {$NUM_NUMBERS} numbers."

# java -cp ./build/classes/java/main java_exp03.tester.DataSenders 2000000
# java -cp ./build/classes/java/main java_exp03.tester.DataSenders 1000000
java -cp ./build/classes/java/main java_exp03.tester.DataSenders $NUM_NUMBERS

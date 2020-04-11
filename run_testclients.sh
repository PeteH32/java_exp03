#!/bin/sh

NUM_NUMBERS=10000
if [ "$1" != "" ]; then
    NUM_NUMBERS=$1
fi

NUM_CLIENTS=1
if [ "$2" != "" ]; then
    NUM_CLIENTS=$2
fi

echo "Sending numbers:\n    NUM_NUMBERS=${NUM_NUMBERS}\n    NUM_CLIENTS=${NUM_CLIENTS}\n"

NUM_FOR_LOOPS="{1..$NUM_CLIENTS}"
for (( i=1; i <= ${NUM_CLIENTS}; i++ ))
do
    echo "Spawning client #${i}:"
    java -cp ./build/classes/java/main java_exp03.tester.DataSenders $NUM_NUMBERS &
done

echo "Done spawning clients.\n"

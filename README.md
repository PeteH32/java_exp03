

## Build and test

- `make`
- Start server:
  - `./run_server.sh`
- In separate Terminal window, run 5 client connections:
  - `./run_testclients.sh 2000000 5`
    - Arg #1: "num_longs_per_client" - number of longs to send, per client
    - Arg #2: "num_clients" - number of clients (which MUST be 5, at this point)
    - Note: Total longs sent will be:
      - Total longs = num_longs_per_client + num_clients

The stats for Requirement #8 will look like this:

```
================================================================================
================================================================================
##########                      REQUIREMENTS #8:                      ##########
Received 9506024 unique numbers, 45093 duplicates. Unique total: 9506024
================================================================================
================================================================================
```

## Old list of possible performance improvements

- Thread pool
  - Use java.util.concurrent.ThreadPoolExecutor so threads can be re-used. Would be good if clients only sends small amount of numbers, so "high client rate".
  - If clients stay connected and send numbers for long time (e.g. 30 seconds or more), this may be unnecessary.
- src/main/java/java_exp03/Server.java
  - Different data structure for "hashsetUniqueLongs". 
    - Would "Long" be better than "String", for the hash table lookups?
      - Collections.synchronizedSet(new HashSet<Long>(INITIAL_CAPACITY));
    - Would "String" be better than "Long", since String would avoid conversion to/from Long?
      - Collections.synchronizedSet(new HashSet<String>(INITIAL_CAPACITY));
      - Would keeping the string at exactly 9 digits (zero-padding) allow some optimization when reading/writing?
- src/main/java/java_exp03/LogFileWriterThread.java
  - Again, would "String" be better than "Long", since String would avoid conversion to/from Long.
- If incoming traffic is bursty, can prioritize reading numbers from clients, and let the log writer fall behind (it can catch back up when incoming traffic slows down).

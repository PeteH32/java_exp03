

## Possible performance improvements

- src/main/java/java_exp03/Server.java
  - Different data structure for "hashsetUniqueLongs". 
    - Would "Long" be better than "String", for the hash table lookups?
      - Collections.synchronizedSet(new HashSet<Long>(INITIAL_CAPACITY));
    - Would "String" be better than "Long", since String would avoid conversion to/from Long?
      - Collections.synchronizedSet(new HashSet<String>(INITIAL_CAPACITY));
      - Would keeping the string at exactly 9 digits (zero-padding) allow some optimization when reading/writing?
- src/main/java/java_exp03/LogFileWriterThread.java
  - Again, would "String" be better than "Long", since String would avoid conversion to/from Long.
- If incoming traffic is bursty, can prioritize reading numbers from clients, and let the log writer fall behind (it can catch back up when incoming traffic slows donw).

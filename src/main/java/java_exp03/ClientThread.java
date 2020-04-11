package java_exp03;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientThread extends Thread {

    public ClientThread(Socket activeSocket, LogFileWriterQueue logWriterQ) {
        super("ClientThread");
        this.activeSocket = activeSocket;
        this.logWriterQ = logWriterQ;
    }

    // WARNING: Be careful with this setting. I only have 8 GBytes of RAM.
    static final int INITIAL_CAPACITY = 100000;
    static Set<String> hashsetUniqueLongs = Collections.synchronizedSet(new HashSet<String>(INITIAL_CAPACITY));

    private Socket activeSocket = null;
    private LogFileWriterQueue logWriterQ;

    // Statistics
    static class Stats {
        static long nRows = 0;
        static long nLongs = 0;
        static long nDupedLongs = 0;
        static long nNotLongs = 0;

        static void printStats() {
            System.out.println("Statistics: ");
            System.out.println("    nRows: " + nRows);
            System.out.println("      nLongs: " + nLongs);
            System.out.println("        nUniqueLongs: " + (nLongs - nDupedLongs));
            System.out.println("        nDupedLongs: " + nDupedLongs);
            System.out.println("      nNotLongs: " + nNotLongs);
        }
    }

    void serveOneConnection() {
        System.out.println("ClientThread: New client connected");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()))) {
            String strLine;
            boolean isLong;
            while (true) {
                strLine = in.readLine();
                Stats.nRows++;

                try {
                    // Verify it is a valid number
                    Long.parseLong(strLine);
                    isLong = true;
                    Stats.nLongs++;
                } catch (final NumberFormatException ex) {
                    System.out.println("ClientThread: Not a long: " + ex.getMessage());
                    isLong = false;
                }

                boolean isNotDupe = false;
                if (isLong) {
                    isNotDupe = hashsetUniqueLongs.add(strLine);
                    if (isNotDupe) {
                        // System.out.println("ClientThread: isNotDupe so enqueuing to writer: " + strLine);
                        logWriterQ.enqueueUniqueLong(strLine);
                    } else {
                        Stats.nDupedLongs++;
                    }
                } else {
                    Stats.nNotLongs++;
                }

                // System.out.printf("ClientThread: row=%s isLong=%b isNotDupe=%b\n", row, isLong, isNotDupe);
                // Check for non-longs: (1) "terminate", or (2) bad input
                if (!isLong) {
                    if (strLine.equals("terminate")) {
                        // 9. If any connected client writes a single line with only the word
                        // "terminate" followed by a server-native newline sequence, the Application
                        // must disconnect all clients and perform a clean shutdown as quickly as
                        // possible.
                        // TODO - When multi-conn, send terminate request to Main thread.
                        System.out.println("ClientThread: Received terminate command.");
                    } else {
                        // Bad input:
                        // 7. Any data that does not conform to a valid line of input should be
                        // discarded and the client connection terminated immediately and without
                        // comment.
                    }
                    break;
                }
            }
            System.out.println("ClientThread: Exiting.");
        } catch (final IOException ex) {
            System.out.println("ClientThread: activeSocket exception: " + ex.getMessage());
        }
        System.out.println("================================================================================");
        System.out.println("================================================================================");
        System.out.println("ClientThread: Client connection ended");
        Stats.printStats();
        System.out.println("================================================================================");
        System.out.println("================================================================================");
    }

    public void run() {
        serveOneConnection();
    }

}
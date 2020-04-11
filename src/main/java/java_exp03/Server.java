package java_exp03;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {

    static long startTime;
    static long endTime;
    static long durationNanosec;

    // WARNING - If you are low on RAM, be careful with this setting. (I only have 8 GBytes total.)
    static final int INITIAL_CAPACITY = 100000;
    static Set<Long> hashsetUniqueLongs = Collections.synchronizedSet(new HashSet<Long>(INITIAL_CAPACITY));

    // Timers
    static void timerStart() {
        startTime = System.nanoTime();
    }

    static void timerStop() {
        endTime = System.nanoTime();
        durationNanosec = (endTime - startTime); // divide by 1,000,000 to get milliseconds.
    }

    static void printDuration() {
        final long durationMillisec = (durationNanosec / 1000000); // divide by 1,000,000 to get milliseconds.
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        // System.out.println("Duration in nanoseconds: " +
        // decimalFormat.format(durationNanosec));
        System.out.println("Duration in milliseconds: " + decimalFormat.format(durationMillisec) + " msec");
    }

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
            System.out.println("        nDupedLongs: " + nDupedLongs);
            System.out.println("      nNotLongs: " + nNotLongs);
        }
    }

    static void serveOneConnection(final Socket activeSocket, final LogFileWriterQueue logWriterQ) {
        System.out.println("New client connected");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()))) {
            timerStart();
            String row;
            long num = -1;
            boolean isLong;
            while (true) {
                row = in.readLine();
                Stats.nRows++;

                try {
                    num = Long.parseLong(row);
                    isLong = true;
                    Stats.nLongs++;
                } catch (final NumberFormatException ex) {
                    System.out.println("Not a long: " + ex.getMessage());
                    isLong = false;
                }

                boolean isNotDupe = false;
                if (isLong) {
                    isNotDupe = hashsetUniqueLongs.add(num);
                    if (isNotDupe) {
                        System.out.println("isNotDupe so enqueuing to writer: " + num);
                        logWriterQ.enqueueUniqueLong(num);
                    } else {
                        Stats.nDupedLongs++;
                    }
                } else {
                    Stats.nNotLongs++;
                }

                // System.out.printf("row=%s  isLong=%b  isNotDupe=%b\n", row, isLong, isNotDupe);
                if (row.equals("terminate")) {
                    timerStop();
                    break;
                }
            }
            System.out.println("Received terminate command. Exiting.\n");
        } catch (final IOException ex) {
            System.out.println("activeSocket exception: " + ex.getMessage());
        }
    }

    public static void main(final String[] args) {
        // Spawn thread to write to log file
        final LogFileWriterQueue logWriterQ = new LogFileWriterQueue(); // in future main could block until this thread
                                                                        // is ready, but not needed at this point
        logWriterQ.start();

        // Loop forever, or until we get the "terminate" message
        final int port = 4000;
        try (ServerSocket passiveSocket = new ServerSocket(port);) {
            System.out.println("Server is listening on port " + port);

            try (Socket activeSocket = passiveSocket.accept()) {
                serveOneConnection(activeSocket, logWriterQ);
            } catch (final IOException ex) {
                System.out.println("activeSocket exception: " + ex.getMessage());
            }
        } catch (final IOException ex) {
            System.out.println("passiveSocket exception: " + ex.getMessage());
            return;
        }
        printDuration();
        Stats.printStats();
        System.out.println();
    }
}
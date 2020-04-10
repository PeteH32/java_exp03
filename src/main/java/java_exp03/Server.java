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

    // WARNING - If you are low on RAM, be careful with this setting.
    static final int INITIAL_CAPACITY = 100000;
    // WARNING - Do not uncomment more than one of the below, otherwise it will take
    // up a lot of RAM.
    static Set<Long> tableSHS_L = Collections.synchronizedSet(new HashSet<Long>(INITIAL_CAPACITY));
    // static Set<String> tableSHS_S = Collections.synchronizedSet(new
    // HashSet<String>(INITIAL_CAPACITY)); // interested if this would be faster
    // than Long
    // static ConcurrentHashMap<Long, Long> tableCHM_L = new ConcurrentHashMap<Long,
    // Long>(INITIAL_CAPACITY); // if ever need to store a V for each K
    // static ConcurrentHashMap<String, Long> tableCHM_S = new
    // ConcurrentHashMap<String, Long>(INITIAL_CAPACITY); // if ever need to store a
    // V for each K

    // Timers
    static void timerStart() {
        startTime = System.nanoTime();
    }

    static void timerStop() {
        endTime = System.nanoTime();
        durationNanosec = (endTime - startTime); // divide by 1,000,000 to get milliseconds.
    }

    static void printDuration() {
        long durationMillisec = (durationNanosec / 1000000); // divide by 1,000,000 to get milliseconds.
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
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

    static void serveOneConnection(Socket activeSocket) {
        System.out.println("New client connected");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));) {
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
                } catch (NumberFormatException ex) {
                    System.out.println("Not a long: " + ex.getMessage());
                    isLong = false;
                }

                boolean isDupe = false;
                if (isLong) {
                    isDupe = !(tableSHS_L.add(num));
                    if (isDupe) {
                        Stats.nDupedLongs++;
                    }
                } else {
                    Stats.nNotLongs++;
                }

                System.out.printf("row=%s  isLong=%b  isDupe=%b\n", row, isLong, isDupe);
                if (row.equals("terminate")) {
                    timerStop();
                    break;
                }
            }
            System.out.println("Received terminate command. Exiting.\n");
        } catch (IOException ex) {
            System.out.println("activeSocket exception: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 4000;

        try (ServerSocket passiveSocket = new ServerSocket(port);) {
            System.out.println("Server is listening on port " + port);

            try (Socket activeSocket = passiveSocket.accept();) {
                serveOneConnection(activeSocket);
            } catch (IOException ex) {
                System.out.println("activeSocket exception: " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println("passiveSocket exception: " + ex.getMessage());
            return;
        }
        printDuration();
        Stats.printStats();
        System.out.println();
    }
}

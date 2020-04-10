package java_exp03;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

public class Server {

    static long startTime;
    static long endTime;
    static long durationNanosec;

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
        System.out.println("Duration in nanoseconds: " + decimalFormat.format(durationNanosec));
        System.out.println("Duration in milliseconds: " + decimalFormat.format(durationMillisec));
    }

    static void serveOneConnection(Socket activeSocket) {
        System.out.println("New client connected");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));) {
            timerStart();
            while (true) {
                String row = in.readLine();
                System.out.printf("row=%s\n", row);
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
    }
}

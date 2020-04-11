package java_exp03.tester;

import java.net.*;
import java.util.Random;
import java.io.*;

public class DataSenders {

    public static void sendDataStream(int nIterations) {
        String hostname = "127.0.0.1";
        int port = 4000;

        Socket socket;
        try {
            socket = new Socket(hostname, port);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
            return;
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return;
        }

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Random rand = new Random();
            System.out.println("Doing iterations: " + nIterations);
            for (int i = 0; i < nIterations; i++) {
                // 9 digits = 0 to 999,999,999 = 1 Billion
                out.printf("%09d\n", rand.nextInt(999999999));
            }
            System.out.println("Sending 'terminate'");
            out.println("terminate");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Exception when closing socket: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        int nIterations = Integer.parseInt(args[0]);
        sendDataStream(nIterations);
    }

}

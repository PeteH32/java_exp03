package java_exp03;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 4000;

        try (ServerSocket passiveSocket = new ServerSocket(port);) {
            System.out.println("Server is listening on port " + port);

            try (Socket activeSocket = passiveSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));) {

                System.out.println("New client connected");
                while (true) {
                    String row = in.readLine();
                    System.out.printf("row=%s\n", row);
                    if (row.equals("terminate")) {
                        break;
                    }
                }
                System.out.println("Received terminate command. Exiting.\n");
            } catch (IOException ex) {
                System.out.println("activeSocket exception: " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println("passiveSocket exception: " + ex.getMessage());
            return;
        }
    }
}

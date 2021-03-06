package java_exp03;

import java.io.*;
import java.net.*;

public class ListenerThread1Client extends ListenerThread {

    public ListenerThread1Client(LogFileWriterQueue logWriterQ) {
        super(logWriterQ);
    }

    public void run() {
        // This class only does one client connection then exits. I used it for early testing.
        boolean isFirstClient = true;
        try (ServerSocket serverSocket = new ServerSocket(4000)) {
            System.out.println("Server is listening on port now");

            try {
                // Wait for client connections.
                Socket socket = serverSocket.accept();
                if (isFirstClient) {
                    isFirstClient = false;
                    timerStart();
                }
                // Spawn thread to read from the client
                final ClientThread clientThread = new ClientThread(socket, logWriterQ);
                // clientThread.start();
                clientThread.serveOneConnection();

                // Tell main to terminate.
                Main.RequestToTerminate();

            } catch (final IOException ex) {
                System.out.println("active socket exception: " + ex.getMessage());
            }
        } catch (final IOException ex) {
            System.out.println("serverSocket exception: " + ex.getMessage());
            return;
        }
        timerStop();
        printDuration();

        System.out.println("ListenerThreadOneClient: Leaving my run() now.");
    }

}
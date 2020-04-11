package java_exp03;

import java.io.*;
import java.net.*;

public class ListenerThread5Client extends ListenerThread {

    public ListenerThread5Client(LogFileWriterQueue logWriterQ) {
        super(logWriterQ);
    }

    // private Thread[] clientThreads = new Thread[5];
    private Thread[] clientThreads = new Thread[1];

    public void run() {
        // This class does multiple clients, currently hard-coded to only max 5 clients.
        boolean isFirstClient = true;
        try (ServerSocket serverSocket = new ServerSocket(4000)) {
            System.out.println("ListenerThread5Client: Server is listening on port now");

            try {
                // Wait for client connections.
                for (int nThread = 0; nThread < clientThreads.length; nThread++) {
                    Socket socket = serverSocket.accept();
                    if (isFirstClient) {
                        isFirstClient = false;
                        timerStart();
                    }
                    // Spawn thread to read from the client
                    final ClientThread clientThread = new ClientThread(socket, logWriterQ);
                    clientThread.start();
                    // clientThread.serveOneConnection(); // for testing
                }

                // Wait for "terminated" message, which will be proceeded by us getting
                // interrupted by Main.main()
                System.out.println("ListenerThread5Client: Going to wait for an interrupt...");
                synchronized (this) {
                    while (!Main.isTerminationRequested()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            System.out.println("ListenerThread5Client: Got an interrupt.");
                        }
                    }
                }
                System.out.println("ListenerThread5Client: Saw there was a request to terminate.");

            } catch (final IOException ex) {
                System.out.println("ListenerThread5Client: active socket exception: " + ex.getMessage());
            }
        } catch (final IOException ex) {
            System.out.println("ListenerThread5Client: serverSocket exception: " + ex.getMessage());
            return;
        }
        timerStop();
        printDuration();

        System.out.println("ListenerThread5Client: Leaving my run() now.");
    }

}
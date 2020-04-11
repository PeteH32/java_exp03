package java_exp03;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListenerThreadPooled extends ListenerThread {

    public ListenerThreadPooled(LogFileWriterQueue logWriterQ) {
        super(logWriterQ);
        final int poolSize = 5;
        pool = Executors.newFixedThreadPool(poolSize);
    }

    private final ExecutorService pool;

    public void run() {
        boolean isFirstClient = true;
        try (ServerSocket serverSocket = new ServerSocket(4000)) {
            System.out.println("Server is listening on port now");

            try {
                // Loop forever, or until we get the "terminate" message
                for (;;) {
                 // Wait for client connections.
                 Socket socket = serverSocket.accept();
                    if (isFirstClient) {
                        isFirstClient = false;
                        timerStart();
                    }
                    // Spawn thread to read from the client
                    pool.execute(new ClientThread(socket, logWriterQ));
                }
            } catch (IOException ex) {
                System.out.println("active socket exception: " + ex.getMessage());
                pool.shutdown();
            }
        } catch (final IOException ex) {
            System.out.println("serverSocket exception: " + ex.getMessage());
            return;
        }

        timerStop();
        printDuration();
    }

}
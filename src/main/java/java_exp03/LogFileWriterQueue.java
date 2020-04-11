package java_exp03;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.LinkedBlockingQueue;

public class LogFileWriterQueue extends Thread {

    public LogFileWriterQueue() {
        super("LogFileWriterQueue");
    }

    public void enqueueUniqueLong(final String row) {
        try {
            // This will block if the queue is full.
            writeQ.put(row);
        } catch (final InterruptedException ex) {
            System.out.println("InterruptedException in enqueueUniqueLong: " + ex.getMessage());
        }
    }

    // WARNING: Be careful with this setting. I only have 8 GBytes of RAM.
    static final int INITIAL_CAPACITY = 500000;
    private final LinkedBlockingQueue<String> writeQ = new LinkedBlockingQueue<String>(INITIAL_CAPACITY);

    public void run() {
        // Open output file
        final Path logfile = Paths.get("numbers.log");
        final Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(logfile, charset)) {
            // Loop forever, or until we get drain queue and get interrupted.
            boolean bShutdownRequested = false;
            while (!bShutdownRequested) {
                try {
                    // System.out.println("LogFileWriterQueue.run: Checking the queue...");

                    // If queue is empty, this will block until something put into it,
                    // or another thread calls interrupt on us.
                    String strNum = writeQ.take();

                    // System.out.println("LogFileWriterQueue.run: Got item from queue: " + strNum);

                    String row = strNum + "\n";
                    writer.write(row, 0, row.length());
                } catch (final InterruptedException ex) {
                    System.out.println("LogFileWriterQueue: InterruptedException when doing writeQ.take(): " + ex.getMessage());
                    // Drain the queue and write all remaining items to our log file.
                    while (!writeQ.isEmpty()) {
                        String strNum = writeQ.remove();
                        String row = strNum + "\n";
                        writer.write(row, 0, row.length());
                    }
                    writer.flush();
                    writer.close();
                    System.out.println("LogFileWriterQueue: Done draining the queue, and flushed the log file.");
                    bShutdownRequested = true;
                }
            }
        } catch (final IOException ex) {
            System.err.format("IOException: %s%n", ex);
        }

        System.out.println("LogFileWriterQueue: Leaving my run() now.");
    }

}
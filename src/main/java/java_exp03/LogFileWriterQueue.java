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

    public void enqueueUniqueLong(final long n) {
        try {
            // This will block if the queue is full.
            writeQ.put(n);
        } catch (final InterruptedException ex) {
            System.out.println("InterruptedException in enqueueUniqueLong: " + ex.getMessage());
        }
    }

    // WARNING - If you are low on RAM, be careful with this setting. (I only have 8
    // GBytes total.)
    static final int INITIAL_CAPACITY = 500000;
    private final LinkedBlockingQueue<Long> writeQ = new LinkedBlockingQueue<Long>(INITIAL_CAPACITY);

    public LogFileWriterQueue() {
        super("LogFileWriter");
    }

    public void run() {
        // Open output file
        final Path logfile = Paths.get("numbers.log");
        final Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(logfile, charset, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {
            // Loop forever, or until we get drain queue and get interrupted.
            long n;
            while (true) {
                // If queue is empty, this will block until something put into it.
                try {
                    System.out.println("LogFileWriterQueue.main: Checking the queue...");
                    n = writeQ.take();
                    System.out.println("LogFileWriterQueue.main: Got item from queue: " + n);
                    final String s = Long.toString(n);
                    System.out.println("LogFileWriterQueue.main: Writing item to file: '" + s + "'  " + s.length());
                    writer.write(s, 0, s.length());
                } catch (final InterruptedException ex) {
                    System.out.println("InterruptedException when doing writeQ.take(): " + ex.getMessage());
                }
            }
        } catch (final IOException ex) {
            System.err.format("IOException: %s%n", ex);
        }

    }

}
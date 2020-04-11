package java_exp03;

public class Main {

    public static void main(final String[] args) {

        // Spawn thread to write to log file
        final LogFileWriterQueue logWriterQ = new LogFileWriterQueue();
        logWriterQ.start();

        // Spawn thread to write to log file
        ListenerThreadOneClient p = new ListenerThreadOneClient(logWriterQ);
        new Thread(p).start();           

    }
}
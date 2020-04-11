package java_exp03;

public class Main {

    // Whichever client gets the "terminate" message, will call this.
    public static void RequestToTerminate() {
        bTerminateRequested = true;
        mainThread.interrupt();
    }
    private static boolean bTerminateRequested = false;
    private static Thread mainThread;

    public static void main(final String[] args) {
        mainThread = Thread.currentThread();

        // Spawn thread to write to log file
        final LogFileWriterQueue logWriterQ = new LogFileWriterQueue();
        logWriterQ.start();

        // Spawn thread to write to log file
        ListenerThreadOneClient r = new ListenerThreadOneClient(logWriterQ);
        Thread t = new Thread(r);
        t.start();

        // Wait for "terminated" message
        synchronized (t) {
            while (!bTerminateRequested) {
                try {
                    t.wait();
                } catch (InterruptedException e) {
                    System.out.println("Main.main: Got an interrupt.");
                }
            }
            System.out.println("Main.main: Got request to terminate.");
        }
        // Tell logWriterQ to empty the queue and exit.
        logWriterQ.interrupt();

        System.out.println("Main.main: Leaving my main() now.");
    }
}
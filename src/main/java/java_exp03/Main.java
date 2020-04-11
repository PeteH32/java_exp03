package java_exp03;

public class Main {

    // Whichever client gets the "terminate" message, will call this.
    public static void RequestToTerminate() {
        bTerminateRequested = true;
        mainThread.interrupt();
    }

    public static boolean isTerminationRequested() {
        return (bTerminateRequested);
    }

    private static boolean bTerminateRequested = false;
    private static Thread mainThread;

    public static void main(final String[] args) {
        mainThread = Thread.currentThread();

        // Spawn thread to write to log file
        final LogFileWriterQueue logWriterQ = new LogFileWriterQueue();
        logWriterQ.start();

        // Spawn thread to listen for client connections
        // ListenerThread r = new ListenerThread1Client(logWriterQ);
        ListenerThread r = new ListenerThread5Client(logWriterQ);
        Thread listenerThread = new Thread(r);
        listenerThread.start();

        // Wait for "terminated" message
        Object obj = new Object();
        synchronized (obj) {
            while (!bTerminateRequested) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    System.out.println("Main.main: Got an interrupt.");
                }
            }
            System.out.println("Main.main: Got request to terminate.");
        }
        // Tell listenerThread to exit.
        listenerThread.interrupt();
        // Tell logWriterQ to empty it's queue, flush it's buffer, and exit.
        logWriterQ.interrupt();

        System.out.println("Main.main: Leaving my main() now.");
    }
}
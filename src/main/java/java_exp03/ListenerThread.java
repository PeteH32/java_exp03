package java_exp03;

import java.text.DecimalFormat;

public abstract class ListenerThread implements Runnable {

    final LogFileWriterQueue logWriterQ;

    public ListenerThread(LogFileWriterQueue logWriterQ) {
        this.logWriterQ = logWriterQ;
    }

    // Timer
    long startTime;
    long endTime;
    long durationNanosec;

    void timerStart() {
        startTime = System.nanoTime();
    }

    void timerStop() {
        endTime = System.nanoTime();
        durationNanosec = (endTime - startTime); // divide by 1,000,000 to get milliseconds.
    }

    void printDuration() {
        final long durationMillisec = (durationNanosec / 1000000); // divide by 1,000,000 to get milliseconds.
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        // System.out.println("Duration in nanoseconds: " +
        // decimalFormat.format(durationNanosec));
        System.out.println("Duration in milliseconds: " + decimalFormat.format(durationMillisec) + " msec");
    }

}
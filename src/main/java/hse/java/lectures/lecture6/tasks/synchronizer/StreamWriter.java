package hse.java.lectures.lecture6.tasks.synchronizer;

import lombok.Getter;

import java.io.PrintStream;

public class StreamWriter implements Runnable {
    @Getter private final String message;
    @Getter private final int id;
    @Getter private final PrintStream output;
    @Getter private final Runnable on_tick;
    private volatile StreamingMonitor monitor;

    public StreamWriter(int id, String message, PrintStream output, Runnable on_tick) {
        this.message = message;
        this.id = id;
        this.output = output;
        this.on_tick = on_tick;
    }

    public void attach_monitor(StreamingMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                monitor.await(id);
                output.print(message);
                on_tick.run();
                monitor.tick_done();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

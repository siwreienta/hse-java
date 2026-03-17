package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public class Synchronizer {
    public static final int DEFAULT_TICKS_PER_WRITER = 10;
    @Getter private final List<StreamWriter> tasks;
    @Getter private final int ticks_per_writer;

    public Synchronizer(List<StreamWriter> tasks) {
        this(tasks, DEFAULT_TICKS_PER_WRITER);
    }

    public Synchronizer(List<StreamWriter> tasks, int ticks_per_writer) {
        this.tasks = tasks;
        this.ticks_per_writer = ticks_per_writer;
    }

    public void execute() throws InterruptedException {
        int[] ids = new int[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) ids[i] = tasks.get(i).getId();
        Arrays.sort(ids);
        StreamingMonitor monitor = new StreamingMonitor(ids, ticks_per_writer);
        for (StreamWriter writer : tasks) writer.attach_monitor(monitor);
        for (StreamWriter writer : tasks) {
            Thread thread = new Thread(writer, "stream-writer-" + writer.getId());
            thread.setDaemon(true);
            thread.start();
        }
        monitor.wait_all();
    }
}

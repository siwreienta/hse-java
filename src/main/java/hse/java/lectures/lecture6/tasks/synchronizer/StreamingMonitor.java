package hse.java.lectures.lecture6.tasks.synchronizer;

import lombok.Getter;

public class StreamingMonitor {
    @Getter private final int[] sorted_ids;
    @Getter private final int[] counts;
    @Getter private final int ticks_per_writer;
    @Getter private int current_idx = 0;
    @Getter private int remaining_ticks;

    public StreamingMonitor(int[] sorted_ids, int ticks_per_writer) {
        this.sorted_ids = sorted_ids;
        this.ticks_per_writer = ticks_per_writer;
        this.counts = new int[sorted_ids.length];
        this.remaining_ticks = sorted_ids.length * ticks_per_writer;
    }

    public synchronized void await(int id) throws InterruptedException {
        while (remaining_ticks > 0 && sorted_ids[current_idx] != id) wait();
        while (remaining_ticks == 0) wait();
    }

    public synchronized void tick_done() {
        counts[current_idx]++;
        remaining_ticks--;
        if (remaining_ticks == 0) {
            notifyAll();
            return;
        }
        int writers_count = sorted_ids.length;
        int next = (current_idx + 1) % writers_count;
        while (counts[next] >= ticks_per_writer) next = (next + 1) % writers_count;
        current_idx = next;
        notifyAll();
    }

    public synchronized void wait_all() throws InterruptedException {
        while (remaining_ticks > 0) wait();
    }
}

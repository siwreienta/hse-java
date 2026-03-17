package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingMonitor {
    private final Map<Integer, Integer> ticks_ = new HashMap<>();
    private final int ticks_per_writer_;
    private final int writers_amount_;
    private int current_idx_ = 0;
    private int total_ticks_ = 0;
    private final int max_ticks_;
    private final List<Integer> sorted_id_;

    public StreamingMonitor(List<Integer> ids, int ticks_per_writer) {
        this.sorted_id_ = ids;
        this.ticks_per_writer_ = ticks_per_writer;
        this.writers_amount_ = ids.size();
        this.max_ticks_ = writers_amount_ * ticks_per_writer_;
        for (Integer id : ids) ticks_.put(id, 0);
    }

    public synchronized boolean allowTick(int id) throws InterruptedException {
        while (!sorted_id_.get(current_idx_).equals(id) && total_ticks_ < max_ticks_) wait();
        if (total_ticks_ >= max_ticks_) {
            notifyAll();
            return false;
        }
        int count = ticks_.get(id);
        if (count >= ticks_per_writer_) {
            current_idx_ = (current_idx_ + 1) % writers_amount_;
            notifyAll();
            return false;
        }
        ticks_.put(id, count + 1);
        total_ticks_++;
        current_idx_ = (current_idx_ + 1) % writers_amount_;
        notifyAll();
        return true;
    }

    public synchronized boolean finished() {
        return total_ticks_ >= max_ticks_;
    }

}

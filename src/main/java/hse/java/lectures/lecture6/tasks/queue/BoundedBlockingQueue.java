package hse.java.lectures.lecture6.tasks.queue;

import java.util.ArrayDeque;
import java.util.Deque;

public class BoundedBlockingQueue<T> {
    private final int capacity_;
    private final Deque<T> data_;


    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("`constructor` argument error: capacity must be positive");
        }
        this.capacity_ = capacity;
        this.data_ = new ArrayDeque<>(capacity);
    }

    public void put(T item) throws InterruptedException {
        if (item == null) {
            throw new NullPointerException("`put` argument error: item cannot be null");
        }
        synchronized (this) {
            while (data_.size() == capacity_) {
                wait();
            }
            data_.addLast(item);
            notifyAll();
        }
    }

    public T take() throws InterruptedException {
        synchronized (this) {
            while (data_.isEmpty()) {
                wait();
            }
            T value = data_.removeFirst();
            notifyAll();
            return value;
        }
    }

    public int size() {
        return data_.size();
    }

    public int capacity() {
        return capacity_;
    }
}

import java.util.ArrayDeque;
import java.util.Deque;

public class BlockingQueue<T> {
    private final Deque<T> deque;
    private final Object lock = new Object();
    private final int capacity;

    public BlockingQueue(int capacity) {
        deque = new ArrayDeque<>(capacity);
        this.capacity = capacity;
    }

    public void enqueue(T elem) {
        synchronized (lock) {
            while (deque.size() >= capacity) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted: " , e);
                }
            }

            deque.addLast(elem);
            lock.notifyAll();
        }
    }

    public T dequeue() {
        synchronized (lock) {
            while (deque.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted: " , e);
                }
            }
            T val = deque.removeFirst();
            lock.notifyAll();
            return val;
        }
    }

    public int size() {
        synchronized (lock) {
            return deque.size();
        }
    }
}

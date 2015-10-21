package hibernate_examples.lang;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class Lock implements ResourceFactory<Void> {

    private final java.util.concurrent.locks.Lock lock;

    public Lock() {
        this(new ReentrantLock());
    }

    public Lock(java.util.concurrent.locks.Lock lock) {
        this.lock = lock;
    }

    @Override
    public <T> T with(Function<Void, T> work) {
        lock.lock();
        try {
            return work.apply(null);
        } finally {
            lock.unlock();
        }
    }
}

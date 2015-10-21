package hibernate_examples.lang;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {

    private final java.util.concurrent.locks.ReadWriteLock lock = new ReentrantReadWriteLock();

    public Lock readLock() {
        return new Lock(lock.readLock());
    }

    public Lock writeLock() {
        return new Lock(lock.writeLock());
    }
}

package hibernate_examples.lang;

import uk.org.lidalia.lang.Exceptions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class Pool<R extends PoolableResource> implements ResourceFactory<R> {

    private final ResourceFactory<R> factory;
    private final ReadWriteLock lock = new ReadWriteLock();

    private final Queue<PoolEntry<R>> idle = new LinkedList<>();
    private final Set<PoolEntry<R>> loaned = new HashSet<>();

    public Pool(ResourceFactory<R> factory) {
        this.factory = factory;
    }

    @Override
    public <T> T with(Function<R, T> work) {
        PoolEntry<R> resource = lock.writeLock().with(this::loan);
        try {
            return work.apply(resource.getResource());
        } finally {
            resource.reset();
            lock.writeLock().with(() -> reclaim(resource));
        }
    }

    private PoolEntry<R> loan() {
        PoolEntry<R> resource = Optional.ofNullable(idle.poll()).orElseGet(this::build);
        loaned.add(resource);
        return resource;
    }

    private PoolEntry<R> build() {
        PoolEntry<R> newResource = new PoolEntry<>();
        final Thread thread = new Thread(() -> {
            factory.with(resource -> {
                newResource.ready(resource);
                newResource.waitUntilNotNeeded();
                return null;
            });
        });
        thread.start();
        return newResource;
    }

    private Void reclaim(PoolEntry<R> resource) {
        loaned.remove(resource);
        idle.add(resource);
        return null;
    }

    void close() {
        lock.writeLock().with(() -> {
            idle.stream().forEach(PoolEntry::notNeeded);
            idle.clear();
            loaned.stream().forEach(PoolEntry::notNeeded);
            loaned.clear();
        });
    }

    static class PoolEntry<R extends PoolableResource> {
        final CountDownLatch ready = new CountDownLatch(1);
        final CountDownLatch finished = new CountDownLatch(1);
        private volatile R resource;
        
        R getResource() {
            try {
                ready.await();
                return resource;
            } catch (InterruptedException e) {
                return Exceptions.throwUnchecked(e, null);
            }
        }
        
        void ready(R resource) {
            this.resource = resource;
            ready.countDown();
        }
        
        void notNeeded() {
            finished.countDown();
        }
        
        void waitUntilNotNeeded() {
            try {
                finished.await();
            } catch (InterruptedException e) {
                Exceptions.throwUnchecked(e);
            }
        }

        void reset() {
            resource.reset();
        }
    }
}

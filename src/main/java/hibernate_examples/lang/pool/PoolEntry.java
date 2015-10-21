package hibernate_examples.lang.pool;

import hibernate_examples.lang.ResourceFactory;
import uk.org.lidalia.lang.Exceptions;

import java.util.concurrent.CountDownLatch;

class PoolEntry<R extends Reusable> {

    private final CountDownLatch ready = new CountDownLatch(1);
    private final CountDownLatch notNeeded = new CountDownLatch(1);
    private final Thread thread;

    private volatile R resource;

    PoolEntry(ResourceFactory<R> factory, Notifier notifier, String poolId) {
        this.thread = new Thread(() -> {
            factory.with(resource -> {
                this.resource = resource;
                ready.countDown();
                notifier.entryCreated(poolId, PoolEntry.this.toString());
                waitUntilNotNeeded();
            });
            notifier.entryDestroyed(poolId, toString());
        });
        this.thread.start();
    }

    R getResource() {
        try {
            ready.await();
            return resource;
        } catch (InterruptedException e) {
            return Exceptions.throwUnchecked(e, null);
        }
    }

    void notNeeded() {
        notNeeded.countDown();
    }

    private void waitUntilNotNeeded() {
        try {
            notNeeded.await();
        } catch (InterruptedException e) {
            Exceptions.throwUnchecked(e);
        }
    }

    void reset() {
        getResource().reset();
    }

    void awaitClosed() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Exceptions.throwUnchecked(e);
        }
    }

    @Override
    public String toString() {
        return super.toString()+'['+getResource()+']';
    }
}

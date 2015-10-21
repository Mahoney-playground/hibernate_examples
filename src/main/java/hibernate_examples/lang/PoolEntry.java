package hibernate_examples.lang;

import uk.org.lidalia.lang.Exceptions;

import java.util.concurrent.CountDownLatch;

class PoolEntry<R extends PoolableResource> {

    private final CountDownLatch ready = new CountDownLatch(1);
    private final CountDownLatch notNeeded = new CountDownLatch(1);
    private final Thread thread;

    private volatile R resource;

    PoolEntry(ResourceFactory<R> factory) {
        this.thread = new Thread(() -> {
            factory.with(resource -> {
                this.resource = resource;
                ready.countDown();
                waitUntilNotNeeded();
                this.resource = null;
            });
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
        return super.toString()+'['+resource+']';
    }
}

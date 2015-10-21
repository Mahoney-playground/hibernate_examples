package hibernate_examples.lang.pool;

import hibernate_examples.lang.ResourceFactory;

import java.util.function.Function;

public class PoolFactory<R extends Reusable> implements ResourceFactory<Pool<R>> {

    private final ResourceFactory<R> factory;
    private final Notifier notifier;

    public PoolFactory(ResourceFactory<R> factory) {
        this(factory, new LoggerNotifier());
    }

    public PoolFactory(ResourceFactory<R> factory, Notifier notifier) {
        this.factory = factory;
        this.notifier = notifier;
    }

    @Override
    public <T> T with(Function<Pool<R>, T> work) {
        Pool<R> pool = new Pool<>(factory, notifier);
        notifier.poolCreated(pool.snapshot());
        try {
            return work.apply(pool);
        } finally {
            pool.close();
            notifier.poolClosed(pool.snapshot());
        }
    }

    @Override
    public String toString() {
        return super.toString()+'['+factory+']';
    }
}

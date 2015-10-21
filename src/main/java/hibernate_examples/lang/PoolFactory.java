package hibernate_examples.lang;

import java.util.function.Function;

public class PoolFactory<R extends PoolableResource> implements ResourceFactory<Pool<R>> {

    private final ResourceFactory<R> factory;

    public PoolFactory(ResourceFactory<R> factory) {
        this.factory = factory;
    }

    @Override
    public <T> T with(Function<Pool<R>, T> work) {
        Pool<R> pool = new Pool<>(factory);
        try {
            return work.apply(pool);
        } finally {
            pool.close();
        }
    }
}

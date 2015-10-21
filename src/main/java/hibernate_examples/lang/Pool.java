package hibernate_examples.lang;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

public class Pool<R extends PoolableResource> implements ResourceFactory<R> {

    private final ResourceFactory<R> factory;
    private final ReadWriteLock lock = new ReadWriteLock();

    private final Queue<PoolEntry<R>> idle = new LinkedList<>();
    private final Set<PoolEntry<R>> loaned = new HashSet<>();

    Pool(ResourceFactory<R> factory) {
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
        return new PoolEntry<>(factory);
    }

    private void reclaim(PoolEntry<R> resource) {
        loaned.remove(resource);
        idle.add(resource);
    }

    void close() {
        lock.writeLock().with(() -> {
            idle.stream().forEach(PoolEntry::notNeeded);
            loaned.stream().forEach(PoolEntry::notNeeded);
            idle.stream().forEach(PoolEntry::awaitClosed);
            loaned.stream().forEach(PoolEntry::awaitClosed);
            idle.clear();
            loaned.clear();
        });
    }
}

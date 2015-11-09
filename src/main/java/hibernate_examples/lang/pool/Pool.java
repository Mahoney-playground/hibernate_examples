package hibernate_examples.lang.pool;

import hibernate_examples.lang.Collectors;
import hibernate_examples.lang.ReadWriteLock;
import hibernate_examples.lang.ResourceFactory;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

public class Pool<R extends Reusable> implements ResourceFactory<R> {

    private final ResourceFactory<R> factory;

    private final Notifier notifier;
    private final ReadWriteLock lock = new ReadWriteLock();

    private final Queue<PoolEntry<R>> idle = new LinkedList<>();
    private final Set<PoolEntry<R>> loaned = new HashSet<>();

    Pool(ResourceFactory<R> factory, Notifier notifier) {
        this.factory = factory;
        this.notifier = notifier;
    }

    @Override
    public <T> T with(Function<R, T> work) {
        PoolEntry<R> entry = lock.writeLock().with(this::loan);
        notifier.entryLoaned(this.snapshot(), entry.toString());
        try {
            return work.apply(entry.getResource());
        } catch(Exception e) {
            entry.onError();
            throw e;
        } finally {
            entry.reset();
            lock.writeLock().with(() -> returns(entry));
            notifier.entryReturned(this.snapshot(), entry.toString());
        }
    }

    private PoolEntry<R> loan() {
        PoolEntry<R> entry = Optional.ofNullable(idle.poll()).orElseGet(this::build);
        loaned.add(entry);
        return entry;
    }

    private PoolEntry<R> build() {
        return new PoolEntry<>(factory, notifier, this);
    }

    private void returns(PoolEntry<R> entry) {
        loaned.remove(entry);
        idle.add(entry);
    }

    void close() {
        idle.stream().forEach(PoolEntry::notNeeded);
        loaned.stream().forEach(PoolEntry::notNeeded);
        idle.stream().forEach(PoolEntry::awaitClosed);
        loaned.stream().forEach(PoolEntry::awaitClosed);
        idle.clear();
        loaned.clear();
    }

    public PoolSnapshot snapshot() {
        return lock.readLock().with(this::takeSnapshot);
    }

    private PoolSnapshot takeSnapshot() {
        return new PoolSnapshot(
            Instant.now(),
            toString(),
            factory.getClass(),
            idle.stream().map(PoolEntry::toString).collect(Collectors.immutableSet()),
            loaned.stream().map(PoolEntry::toString).collect(Collectors.immutableSet())
        );
    }

    @Override
    public String toString() {
        return super.toString()+'['+factory+']';
    }
}

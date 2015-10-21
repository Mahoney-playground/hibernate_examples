package hibernate_examples.lang.pool;

import com.google.common.collect.ImmutableSet;

import java.time.Instant;
import java.util.Objects;

public final class PoolSnapshot {

    private final Instant time;
    private final String pool;
    private final Class<?> poolType;
    private final ImmutableSet<String> idle;
    private final ImmutableSet<String> loaned;

    public PoolSnapshot(
        Instant time,
        String pool,
        Class<?> poolType,
        ImmutableSet<String> idle,
        ImmutableSet<String> loaned
    ) {
        this.time = time;
        this.pool = pool;
        this.poolType = poolType;
        this.idle = idle;
        this.loaned = loaned;
    }

    public Instant getTime() {
        return time;
    }

    public String getPool() {
        return pool;
    }

    public Class<?> getPoolType() {
        return poolType;
    }

    public ImmutableSet<String> getIdle() {
        return idle;
    }

    public ImmutableSet<String> getLoaned() {
        return loaned;
    }

    public int idleSize() {
        return idle.size();
    }

    public int loanedSize() {
        return loaned.size();
    }

    public int size() {
        return idleSize() + loanedSize();
    }

    @Override
    public String toString() {
        return "PoolSnapshot{" +
                "time=" + time +
                ", pool=" + pool +
                ", idle=" + idle.size() +
                ", loaned=" + loaned.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoolSnapshot that = (PoolSnapshot) o;
        return Objects.equals(time, that.time) &&
                Objects.equals(pool, that.pool) &&
                Objects.equals(poolType, that.poolType) &&
                Objects.equals(idle, that.idle) &&
                Objects.equals(loaned, that.loaned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, pool, poolType, idle, loaned);
    }
}

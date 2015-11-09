package hibernate_examples;

import hibernate_examples.lang.ResourceFactory;
import hibernate_examples.lang.pool.PoolFactory;
import hibernate_examples.lang.pool.PoolSnapshot;
import hibernate_examples.lang.pool.Reusable;
import hibernate_examples.model.Parent;
import org.junit.Test;
import uk.org.lidalia.test.ShouldThrow;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static uk.org.lidalia.test.ShouldThrow.shouldThrow;

public class PoolFactoryTest {

    private final PoolFactory<TestResource> poolFactory = new PoolFactory<>(new ResourceFactory<TestResource>() {
        @Override
        public <T> T with(Function<TestResource, T> work) {
            TestResource resource = new TestResource();
            try {
                return work.apply(resource);
            } finally {
                resource.close();
            }
        }
    });

    @Test
    public void returnsValue() throws Exception {

        poolFactory.with(pool -> {

            String first = pool.with(resource -> {
                return resource.use(() -> "first");
            });

            assertEquals("first", first);
        });
    }

    @Test
    public void throwsException() throws Exception {

        poolFactory.with(pool -> {

            IllegalStateException actual = shouldThrow(IllegalStateException.class, () -> {
                pool.with(resource -> {
                    return resource.use(() -> {
                        throw new IllegalStateException("FAILED");
                    });
                });
            });

            assertEquals("FAILED", actual.getMessage());
        });
    }

    @Test
    public void reusesResource() throws Exception {

        Set<TestResource> resources = new LinkedHashSet<>();

        poolFactory.with(pool -> {

            pool.with(resource -> {
                resources.add(resource);
                return resource.use(() -> "first");
            });

            pool.with(resource -> {
                resources.add(resource);
                return resource.use(() -> "second");
            });

            assertEquals(1, resources.size());
            assertEquals(2, resources.stream().findFirst().get().timesUsed.get());
        });
    }

    @Test
    public void reusesResourceAfterException() throws Exception {

        Set<TestResource> resources = new LinkedHashSet<>();

        poolFactory.with(pool -> {

            shouldThrow(IllegalStateException.class, () -> {
                pool.with(resource -> {
                    resources.add(resource);
                    return resource.use(() -> {
                        throw new IllegalStateException("FAILED");
                    });
                });
            });

            String second = pool.with(resource -> {
                resources.add(resource);
                return resource.use(() -> "second");
            });

            assertEquals("second", second);
            assertEquals(1, resources.size());
            assertEquals(2, resources.stream().findFirst().get().timesUsed.get());
        });
    }

    private static class TestResource implements Reusable {

        final AtomicInteger timesUsed = new AtomicInteger(0);
        volatile State state = State.OK;

        @Override
        public State check() {
            return state;
        }

        @Override
        public void reset() {
            state = State.OK;
        }

        public <T> T use(Supplier<T> supplier) {
            timesUsed.incrementAndGet();
            return supplier.get();
        }

        public void close() {
            state = State.CLOSED;
        }
    }
}

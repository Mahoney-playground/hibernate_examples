package hibernate_examples.lang;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ResourceFactory<R> {

    <T> T with(Function<R, T> work);

    default <T> T with(Supplier<T> work) {
        return with((Function<R, T>) ignore -> work.get());
    }

    default void with(Consumer<R> work) {
        with(resource -> {
            work.accept(resource);
            return null;
        });
    }

    default void with(Runnable work) {
        with(ignore -> {
            work.run();
            return null;
        });
    }
}

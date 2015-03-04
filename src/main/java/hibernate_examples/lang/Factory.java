package hibernate_examples.lang;

import java.util.function.Function;
import java.util.function.Supplier;

public class Factory<R extends Resource> {

    private final Supplier<R> source;

    public Factory(Supplier<R> source) {
        this.source = source;
    }

    public <T> T with(Function<R, T> work) {
        try (R resource = source.get()) {
            return work.apply(resource);
        }
    }
}

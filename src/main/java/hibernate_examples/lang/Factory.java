package hibernate_examples.lang;

import java.util.function.Function;
import java.util.function.Supplier;

public class Factory<R> {

    private final Supplier<CloseableResource<R>> source;

    public Factory(Supplier<CloseableResource<R>> source) {
        this.source = source;
    }

    public <T> T with(Function<R, T> work) {
        try (CloseableResource<R> resource = source.get()) {
            return work.apply(resource.toResource());
        }
    }
}

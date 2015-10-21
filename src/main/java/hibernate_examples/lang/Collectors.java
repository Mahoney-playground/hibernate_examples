package hibernate_examples.lang;

import com.google.common.collect.ImmutableSet;

import java.util.stream.Collector;

public class Collectors {

    public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> immutableSet() {
        return Collector.of(ImmutableSet.Builder::new, ImmutableSet.Builder::add,
                (l, r) -> l.addAll(r.build()), ImmutableSet.Builder<T>::build);
    }
}

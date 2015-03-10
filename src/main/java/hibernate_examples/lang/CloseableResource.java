package hibernate_examples.lang;

public interface CloseableResource<R> extends AutoCloseable {

    @Override
    void close();

    public R toResource();
}

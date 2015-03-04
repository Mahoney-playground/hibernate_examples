package hibernate_examples.lang;

public interface Resource extends AutoCloseable {

    @Override
    void close();
}

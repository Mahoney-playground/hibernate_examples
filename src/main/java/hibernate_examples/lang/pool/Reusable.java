package hibernate_examples.lang.pool;

public interface Reusable {

    enum State { BROKEN, OK }

    State check();

    void reset();

}

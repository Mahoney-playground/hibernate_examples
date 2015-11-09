package hibernate_examples.lang.pool;

public interface Reusable {

    enum State { BROKEN, OK, CLOSED, DIRTY }

    default State check() { return State.OK; }

    default void onError() { reset(); }

    default void reset() {}

}

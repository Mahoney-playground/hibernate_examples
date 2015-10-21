package hibernate_examples.lang.pool;

public interface Notifier {

    enum EventType {
        POOL_CREATED,
        POOL_CLOSED,
        ENTRY_CREATED,
        ENTRY_DESTROYED,
        ENTRY_LOANED,
        ENTRY_RETURNED
    }

    void entryEvent(EventType event, PoolSnapshot snapshot, String entryId);
    void poolEvent(EventType event, PoolSnapshot snapshot);

    default void poolCreated(PoolSnapshot snapshot) {
        poolEvent(EventType.POOL_CREATED, snapshot);
    }

    default void poolClosed(PoolSnapshot snapshot) {
        poolEvent(EventType.POOL_CLOSED, snapshot);
    }

    default void entryCreated(PoolSnapshot snapshot, String entry) {
        entryEvent(EventType.ENTRY_CREATED, snapshot, entry);
    }

    default void entryDestroyed(PoolSnapshot snapshot, String entry) {
        entryEvent(EventType.ENTRY_DESTROYED, snapshot, entry);
    }

    default void entryLoaned(PoolSnapshot snapshot, String entry) {
        entryEvent(EventType.ENTRY_LOANED, snapshot, entry);
    }

    default void entryReturned(PoolSnapshot snapshot, String entry) {
        entryEvent(EventType.ENTRY_RETURNED, snapshot, entry);
    }
}

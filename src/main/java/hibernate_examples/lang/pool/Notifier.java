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

    void entryEvent(EventType event, String poolId, String entryId);
    void poolEvent(EventType event, String poolId);

    default void poolCreated(String poolId) {
        poolEvent(EventType.POOL_CREATED, poolId);
    }

    default void poolClosed(String poolId) {
        poolEvent(EventType.POOL_CLOSED, poolId);
    }

    default void entryCreated(String poolId, String entry) {
        entryEvent(EventType.ENTRY_CREATED, poolId, entry);
    }

    default void entryDestroyed(String poolId, String entry) {
        entryEvent(EventType.ENTRY_DESTROYED, poolId, entry);
    }

    default void entryLoaned(String poolId, String entry) {
        entryEvent(EventType.ENTRY_LOANED, poolId, entry);
    }

    default void entryReturned(String poolId, String entry) {
        entryEvent(EventType.ENTRY_RETURNED, poolId, entry);
    }
}

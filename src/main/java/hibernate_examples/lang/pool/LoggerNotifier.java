package hibernate_examples.lang.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerNotifier implements Notifier {

    private final Logger log;

    public LoggerNotifier() {
        this(LoggerFactory.getLogger(LoggerNotifier.class));
    }

    public LoggerNotifier(Logger logger) {
        this.log = logger;
    }

    @Override
    public void entryEvent(EventType event, PoolSnapshot snapshot, String id) {
        log.debug("{}: {} of pool {}", event, id, snapshot.getPool());
    }

    @Override
    public void poolEvent(EventType event, PoolSnapshot snapshot) {
        log.info("{}: {}", event, snapshot.getPool());
    }

    @Override
    public void entryCreated(PoolSnapshot snapshot, String entry) {
        log.info("{}: {} of pool {}", EventType.ENTRY_CREATED, entry, snapshot.getPool());
    }

    @Override
    public void entryDestroyed(PoolSnapshot snapshot, String entry) {
        log.info("{}: {} of pool {}", EventType.ENTRY_DESTROYED, entry, snapshot.getPool());
    }
}

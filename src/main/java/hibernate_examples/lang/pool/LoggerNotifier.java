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
    public void entryEvent(EventType event, String poolId, String id) {
        log.debug("{}: {} of pool {}", event, id, poolId);
    }

    @Override
    public void poolEvent(EventType event, String poolId) {
        log.info("{}: {}", event, poolId);
    }

    @Override
    public void entryCreated(String poolId, String entry) {
        log.info("{}: {} of pool {}", EventType.ENTRY_CREATED, entry, poolId);
    }

    @Override
    public void entryDestroyed(String poolId, String entry) {
        log.info("{}: {} of pool {}", EventType.ENTRY_DESTROYED, entry, poolId);
    }
}

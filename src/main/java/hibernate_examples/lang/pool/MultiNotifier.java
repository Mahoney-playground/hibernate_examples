package hibernate_examples.lang.pool;

import com.google.common.collect.ImmutableSet;

public class MultiNotifier implements Notifier {
    
    private final ImmutableSet<Notifier> notifiers;

    public MultiNotifier(Notifier... notifiers) {
        this(ImmutableSet.copyOf(notifiers));
    }

    public MultiNotifier(ImmutableSet<Notifier> notifiers) {
        this.notifiers = notifiers;
    }

    @Override
    public void entryEvent(EventType event, PoolSnapshot snapshot, String id) {
        notifiers.stream().forEach( n -> n.entryEvent(event, snapshot, id));
    }

    @Override
    public void poolEvent(EventType event, PoolSnapshot snapshot) {
        notifiers.stream().forEach( n -> n.poolEvent(event, snapshot));
    }
}

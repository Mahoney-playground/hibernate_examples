package hibernate_examples.hibernate;

import hibernate_examples.lang.Resource;
import org.hibernate.HibernateException;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

public class Session implements Resource {

    private final org.hibernate.Session session;

    Session(org.hibernate.Session session) {
        this.session = session;
    }

    @Override
    public void close() throws HibernateException {
        try {
            session.flush();
        } finally {
            session.close();
        }
    }

    public <T extends Entity> Optional<T> get(Class<T> type, UUID id) {
        return ofNullable(doGet(type, id));
    }

    public <T extends Entity> T load(Class<T> type, UUID id) {
        return checkNotNull(
                doGet(type, id),
                "Failed to load %s for id %s", type, id
        );
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> T doGet(Class<T> type, UUID id) {
        return (T) session.get(type, id);
    }

    public <T extends Entity> T save(T transientEntity) {
        session.saveOrUpdate(transientEntity);
        return transientEntity;
    }

    public void delete(Entity entity) {
        session.delete(entity);
    }

    public <T> Collection<? extends T> loadAll(Class<T> type) {
        return session.createCriteria(type).list();
    }
}

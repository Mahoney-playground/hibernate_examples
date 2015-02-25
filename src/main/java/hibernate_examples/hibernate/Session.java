package hibernate_examples.hibernate;

import org.hibernate.HibernateException;

import java.io.Serializable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

public class Session implements AutoCloseable {

    private final org.hibernate.Session session;

    public Session(org.hibernate.Session session) {
        this.session = session;
    }

    @Override
    public void close() throws HibernateException {
        session.flush();
        session.close();
    }

    public <T> Optional<T> get(Class<T> type, Serializable id) {
        return ofNullable(doGet(type, id));
    }

    public <T> T load(Class<T> type, Serializable id) {
        return checkNotNull(
                doGet(type, id),
                "Failed to load %s for id %s", type, id
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T doGet(Class<T> type, Serializable id) {
        return (T) session.get(type, id);
    }

    public Serializable save(Entity entity) {
        return session.save(entity);
    }

    public void delete(Entity entity) {
        session.delete(entity);
    }
}

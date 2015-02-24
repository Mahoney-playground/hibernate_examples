package hibernate_examples.hibernate;

import java.util.function.Function;

public class SessionFactory {

    private final org.hibernate.SessionFactory sessionFactory;


    public SessionFactory(org.hibernate.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T withSession(Function<Session, T> work) {
        try (Session session = new Session(sessionFactory.openSession())) {
            return work.apply(session);
        }
    }
}

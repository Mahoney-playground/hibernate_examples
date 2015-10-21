package hibernate_examples.hibernate;

import hibernate_examples.lang.ResourceFactory;

import java.util.function.Function;

public class SessionFactory implements ResourceFactory<Session> {

    private final org.hibernate.SessionFactory sessionFactory;

    public SessionFactory(org.hibernate.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T with(Function<Session, T> work) {
        Session session = new Session(sessionFactory.openSession());
        try {
            return work.apply(session);
        } finally {
            session.close();
        }
    }
}

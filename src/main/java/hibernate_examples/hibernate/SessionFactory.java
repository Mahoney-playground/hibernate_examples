package hibernate_examples.hibernate;

import hibernate_examples.lang.Factory;

public class SessionFactory extends Factory<Session> {

    public SessionFactory(org.hibernate.SessionFactory sessionFactory) {
        super(() -> new CloseableSession(new Session(sessionFactory.openSession())));
    }
}

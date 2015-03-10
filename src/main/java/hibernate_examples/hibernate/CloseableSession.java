package hibernate_examples.hibernate;

import hibernate_examples.lang.CloseableResource;

class CloseableSession implements CloseableResource<Session> {

    private final Session session;

    CloseableSession(Session session) {
        this.session = session;
    }

    @Override
    public void close() {
        session.close();
    }

    @Override
    public Session toResource() {
        return session;
    }
}

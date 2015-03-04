package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Parent;
import org.hibernate.StaleObjectStateException;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class OptimisticLockingTest {

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();

    @Test
    public void changePriorToReattachingObject() throws Exception {
        Parent detached = sessionFactory.with(session -> {
            Parent parent = new Parent("parent");
            session.save(parent);
            return parent;
        });

        sessionFactory.with(session -> {
            Parent parent = session.load(Parent.class, detached.getId());
            parent.setMutableState("new stuff!");
            return null;
        });

        try {
            sessionFactory.with(session -> {
                session.save(detached);
                detached.setMutableState("other stuff - shouldn't work!");
                return null;
            });
            fail("Should throw StaleObjectStateException");
        } catch (StaleObjectStateException e) {
            // expected
        }

        sessionFactory.with(session -> {
            Parent parent = session.load(Parent.class, detached.getId());
            assertThat(parent.getMutableState(), is("new stuff!"));
            return null;
        });
    }

    @Test
    public void changeDuringSession() throws Exception {
        UUID parentId = sessionFactory.with(session -> {
            Parent parent = new Parent("parent");
            parent.setMutableState("Original value");
            return session.save(parent).getId();
        });

        try {
            sessionFactory.with(session -> {
                Parent parent = session.load(Parent.class, parentId);
                sessionFactory.with(concurrentSession -> {
                    Parent concurrentParent = concurrentSession.load(Parent.class, parentId);
                    concurrentParent.setMutableState("new stuff!");
                    return null;
                });
                parent.setMutableState("shouldn't work, out of date!");
                return null;
            });
            fail("Should throw StaleObjectStateException");
        } catch (StaleObjectStateException e) {
            // expected
        }

        sessionFactory.with(session -> {
            Parent parent = session.load(Parent.class, parentId);
            assertThat(parent.getMutableState(), is("new stuff!"));
            return null;
        });
    }
}

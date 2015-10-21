package hibernate_examples;

import hibernate_examples.hibernate.Session;
import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.lang.PoolFactory;
import hibernate_examples.model.Parent;
import org.junit.Test;

import java.util.UUID;

import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PoolFactoryTest {

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();
    private final PoolFactory<Session> sessionPoolFactory = new PoolFactory<>(sessionFactory);

    @Test
    public void createAndRetrieveParentWithNoChildren() throws Exception {
        final UUID parentId = sessionFactory.with(session -> {
            Parent parent = new Parent("parent");
            session.save(parent);
            return parent.getId();
        });

        sessionPoolFactory.with(pool -> {
            pool.with(session -> {
                final Parent parent = session.load(Parent.class, parentId);
                Parent expected = new Parent("parent");
                assertThat(parent, is(expected));
                assertThat(parent.getChildren(), is(emptySet()));
            });
        });
    }
}

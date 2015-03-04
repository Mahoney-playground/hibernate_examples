package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Parent;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ReattachObjectTest {

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();

    @Test
    public void reattachObject() throws Exception {
        Parent detached = sessionFactory.with(session -> {
            Parent parent = new Parent("parent");
            session.save(parent);
            return parent;
        });

        sessionFactory.with(session -> {
            detached.setMutableState("new stuff!");
            return session.save(detached);
        });

        sessionFactory.with(session -> {
            Parent parent = session.load(Parent.class, detached.getId());
            assertThat(parent.getMutableState(), is("new stuff!"));
            System.out.println(session.loadAll(Parent.class));
            return null;
        });
    }
}

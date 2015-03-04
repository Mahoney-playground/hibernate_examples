package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Child;
import hibernate_examples.model.Parent;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ChildEqualsTest {

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();

    @Test
    public void comparePersistedAndUnpersistedChild() throws Exception {
        final UUID childId = sessionFactory.with(session -> {
            Parent parent = new Parent("parent");
            session.save(parent);
            return parent.addChild("child").getId();
        });

        sessionFactory.with(session -> {
            Parent unpersistedParent = new Parent("parent");
            Child unpersistedChild = unpersistedParent.addChild("child");

            final Child child = session.load(Child.class, childId);

            assertThat(child, is(unpersistedChild));
            assertThat(unpersistedChild, is(child));
            assertThat(child.hashCode(), is(unpersistedChild.hashCode()));
            return null;
        });
    }
}

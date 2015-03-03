package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Child;
import hibernate_examples.model.Parent;
import org.junit.Test;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.of;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ChildEqualsTest {

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();

    @Test
    public void comparePersistedAndUnpersistedChild() throws Exception {
        final Serializable childId = sessionFactory.withSession(session -> {
            Parent parent = new Parent("parent");
            session.save(parent);
            return parent.addChild("child").getId();
        });

        sessionFactory.withSession(session -> {
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

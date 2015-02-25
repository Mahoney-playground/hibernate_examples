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

public class ReplaceChildTest {

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();

    @Test
    public void createAndRetrieveParentWithNoChildren() throws Exception {
        final Serializable parentId = sessionFactory.withSession(session -> session.save(new Parent("parent")));

        sessionFactory.withSession(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            Parent expected = new Parent("parent");
            assertThat(parent, is(expected));
            assertThat(parent.getChildren(), is(emptySet()));
            return null;
        });
    }

    @Test
    public void createAndRetrieveParent() throws Exception {
        final Serializable parentId = sessionFactory.withSession(session -> {
            final Parent parent = new Parent("parent");
            parent.addChild("child");
            return session.save(parent);
        });

        sessionFactory.withSession(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            Parent expected = new Parent("parent");
            expected.addChild("child");
            assertThat(parent, is(expected));
            return null;
        });
    }

    @Test
    public void replaceChild() throws SQLException {

        testReplaceChild("child1", "child2");
    }

    @Test
    public void replaceChildWithSame() throws SQLException {

        testReplaceChild("child1", "child1");
    }

    private void testReplaceChild(final String initialChildName, final String newChildName) {

        final Serializable parentId = sessionFactory.withSession(session -> {
            final Parent parent = new Parent("parent");
            parent.addChild(initialChildName);
            return session.save(parent);
        });

        sessionFactory.withSession(session -> {
            session.load(Parent.class, parentId)
                    .replaceChild(newChildName);
            return null;
        });

        sessionFactory.withSession(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            final Set<String> namesOfChildren = parent.getChildren().stream().map(Child::getName).collect(toSet());
            assertThat(namesOfChildren, is(of(newChildName)));
            return null;
        });
    }

}

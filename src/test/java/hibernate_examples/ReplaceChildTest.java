package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Child;
import hibernate_examples.model.Parent;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.ImmutableSet.of;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ReplaceChildTest {

    private final SessionFactory sessionFactory = TestSessionFactory.buildSessionFactory();

    @Test
    public void createAndRetrieveParentWithNoChildren() throws Exception {
        final UUID parentId = sessionFactory.with(session -> {
            Parent parent = new Parent("parent");
            session.save(parent);
            return parent.getId();
        });

        sessionFactory.with(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            Parent expected = new Parent("parent");
            assertThat(parent, is(expected));
            assertThat(parent.getChildren(), is(emptySet()));
        });
    }

    @Test
    public void createAndRetrieveParent() throws Exception {
        final UUID parentId = sessionFactory.with(session -> {
            final Parent parent = new Parent("parent");
            parent.addChild("child");
            session.save(parent);
            return parent.getId();
        });

        sessionFactory.with(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            Parent expected = new Parent("parent");
            expected.addChild("child");
            assertThat(parent, is(expected));
        });
    }

    @Test
    public void replaceAllChildren() throws SQLException {

        testReplaceAllChildren("child1", "child2");
    }

    @Test
    public void replaceAllChildrenWithSame() throws SQLException {

        testReplaceAllChildren("child1", "child1");
    }

    private void testReplaceAllChildren(final String initialChildName, final String newChildName) {

        final UUID parentId = sessionFactory.with(session -> {
            final Parent parent = new Parent("parent");
            parent.addChild("unchanged");
            parent.addChild(initialChildName);
            session.save(parent);
            return parent.getId();
        });

        sessionFactory.with(session -> {
            session.load(Parent.class, parentId)
                    .replaceAllChildrenWith(newChildName);
        });

        sessionFactory.with(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            final Set<String> namesOfChildren = parent.getChildren().stream().map(Child::getName).collect(toSet());
            assertThat(namesOfChildren, is(of(newChildName)));
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

        final UUID parentId = sessionFactory.with(session -> {
            final Parent parent = new Parent("parent");
            parent.addChild("unchanged");
            parent.addChild(initialChildName);
            session.save(parent);
            return parent.getId();
        });

        sessionFactory.with(session -> {
            session.load(Parent.class, parentId)
                    .replaceChild(initialChildName, newChildName);
        });

        sessionFactory.with(session -> {
            final Parent parent = session.load(Parent.class, parentId);
            final Set<String> namesOfChildren = parent.getChildren().stream().map(Child::getName).collect(toSet());
            assertThat(namesOfChildren, is(of("unchanged", newChildName)));
        });
    }
}

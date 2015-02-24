package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Child;
import hibernate_examples.model.Parent;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.of;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ReplaceChildWithSameName {

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        TestLoggerFactory.getInstance().setPrintLevel(Level.DEBUG);
    }

    private final SessionFactory sessionFactory = new SessionFactory(
            new Configuration()
                    .setProperty("hibernate.hbm2ddl.auto", "create")
                    .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
                    .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
                    .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:"+ RandomStringUtils.randomAlphanumeric(5))
                    .setProperty("hibernate.connection.username", "sa")
                    .setProperty("hibernate.connection.password", "")
                    .addAnnotatedClass(Child.class)
                    .addAnnotatedClass(Parent.class)
                    .buildSessionFactory()
    );

    @Test
    public void replaceChild() throws SQLException {

        testReplaceChild("child1", "child2");
    }

    @Test
    public void replaceChildWithSame() throws SQLException {

        testReplaceChild("child1", "child1");
    }

    private void testReplaceChild(String initialChildName, String newChildName) {
        final Serializable parentId = sessionFactory.withSession(session -> {
            Parent parent = new Parent("parent");
            parent.addChild(initialChildName);
            return session.save(parent);
        });

        sessionFactory.withSession(session -> {
            session.load(Parent.class, parentId)
                    .replaceChild(newChildName);
            return null;
        });

        sessionFactory.withSession(session -> {
            Parent parent = session.load(Parent.class, parentId);
            Set<String> namesOfChildren = parent.getChildren().stream().map(Child::getName).collect(toSet());
            assertThat(namesOfChildren, is(of(newChildName)));
            return null;
        });
    }

}

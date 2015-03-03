package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Child;
import hibernate_examples.model.Parent;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class TestSessionFactory {

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    public static SessionFactory buildSessionFactory() {
        return new SessionFactory(buildHibernateSessionFactory());
    }

    private static org.hibernate.SessionFactory buildHibernateSessionFactory() {
        Configuration configuration = getConfiguration();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        return configuration
                .buildSessionFactory(serviceRegistry);
    }

    private static Configuration getConfiguration() {
        return new Configuration()
                .setProperty("hibernate.hbm2ddl.auto", "create")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
                .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:" + RandomStringUtils.randomAlphanumeric(5))
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .addAnnotatedClass(Child.class)
                .addAnnotatedClass(Parent.class);
    }
}

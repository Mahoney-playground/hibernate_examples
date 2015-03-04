package hibernate_examples;

import hibernate_examples.hibernate.SessionFactory;
import hibernate_examples.model.Child;
import hibernate_examples.model.Parent;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hsqldb.jdbc.JDBCDataSource;
import uk.org.lidalia.lang.Exceptions;

import java.sql.Connection;
import java.sql.SQLException;

import static org.hibernate.cfg.AvailableSettings.DATASOURCE;
import static uk.org.lidalia.lang.Exceptions.throwUnchecked;

public class TestSessionFactory {

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    public static SessionFactory buildSessionFactory() {
        return new SessionFactory(buildHibernateSessionFactory());
    }

    private static org.hibernate.SessionFactory buildHibernateSessionFactory() {
        Configuration configuration = buildConfiguration();
        JDBCDataSource dataSource = buildDataSource();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .applySetting(DATASOURCE, dataSource)
                .build();
        return configuration
                .buildSessionFactory(serviceRegistry);
    }

    private static JDBCDataSource buildDataSource() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:" + RandomStringUtils.randomAlphabetic(5));
        dataSource.setUser("sa");
        dataSource.setPassword("");
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("set database sql syntax MYS TRUE;").execute();
            conn.prepareStatement("set database transaction control MVCC;").execute();
        } catch (SQLException e) {
            return throwUnchecked(e, null);
        }
        return dataSource;
    }

    private static Configuration buildConfiguration() {
        return new Configuration()
                .setProperty("hibernate.hbm2ddl.auto", "create")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .addAnnotatedClass(Child.class)
                .addAnnotatedClass(Parent.class);
    }
}

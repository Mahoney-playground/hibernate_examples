package hibernate_examples.hibernate;

import org.hibernate.Hibernate;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@MappedSuperclass
public abstract class Entity {

    @Id
    protected UUID id = randomUUID();

    @Version
    protected Long version = null;

    protected static Class getClass(Object proxy) {
        return Hibernate.getClass(proxy);
    }

    public UUID getId() {
        return id;
    }

    // Solely for Hibernate
    protected Entity() {
    }
}

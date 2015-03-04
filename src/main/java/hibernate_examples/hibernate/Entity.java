package hibernate_examples.hibernate;

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

    public UUID getId() {
        return id;
    }

    // Solely for Hibernate
    protected Entity() {
    }
}

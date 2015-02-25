package hibernate_examples.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"NAME", "PARENT_ID"}))
public class Child {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = LAZY, optional = false)
    private Parent parent;

    Child(Parent parent, String name) {
        this.id = randomUUID();
        this.parent = checkNotNull(parent);
        this.name = checkNotNull(name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Parent getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Child child = (Child) o;

        return  name.equals(child.name) &&
                parent.equals(child.parent);
    }

    @Override
    public int hashCode() {
        return hash(name, parent);
    }

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                '}';
    }

    private Child() {}
}

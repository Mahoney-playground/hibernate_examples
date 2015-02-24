package hibernate_examples.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"NAME", "PARENT_ID"}))
public class Child {

    @Id
    private UUID id;

    private String name;

    @ManyToOne(fetch = LAZY)
    private Parent parent;

    Child(Parent parent, String name) {
        this.id = UUID.randomUUID();
        this.parent = parent;
        this.name = name;
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

        if (!name.equals(child.name)) return false;
        if (!parent.equals(child.parent)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + parent.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                '}';
    }

    private Child() {}
}

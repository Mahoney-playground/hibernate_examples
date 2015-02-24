package hibernate_examples.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class Parent {

    @Id
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Child> children = new HashSet<>();

    public Parent(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Child> getChildren() {
        return children;
    }

    public Child addChild(String name) {
        final Child child = new Child(this, name);
        children.add(child);
        return child;
    }

    public void replaceChild(String name) {
        children.clear();
        addChild(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Parent child = (Parent) o;

        if (!name.equals(child.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Parent{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }

    // Solely for Hibernate
    private Parent() {
    }
}

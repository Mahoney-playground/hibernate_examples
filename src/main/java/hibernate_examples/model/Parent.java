package hibernate_examples.model;

import com.google.common.collect.ImmutableSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.CascadeType.ALL;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class Parent implements hibernate_examples.hibernate.Entity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
    private Set<Child> children;

    public Parent(String name) {
        this.id = UUID.randomUUID();
        this.name = checkNotNull(name);
        this.children = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<Child> getChildren() {
        return ImmutableSet.copyOf(children);
    }

    public Child addChild(String name) {
        final Child child = new Child(this, name);
        if (children.add(child))
            return child;
        else
            return children.stream().filter(candidate -> child.equals(candidate)).findFirst().get();
    }

    public Child replaceChild(String initialChildName, String newChildName) {
        children.removeIf(child -> child.getName().equals(initialChildName));
        return addChild(newChildName);
    }

    public Child replaceAllChildrenWith(String name) {
        children.clear();
        return addChild(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Parent child = (Parent) o;

        return name.equals(child.name);
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

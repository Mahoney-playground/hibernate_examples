package hibernate_examples.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableSet;
import static javax.persistence.CascadeType.ALL;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class Parent extends hibernate_examples.hibernate.Entity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mutableState;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
    private Set<Child> children;

    public Parent(String name) {
        this.name = checkNotNull(name);
        this.children = new HashSet<>();
        this.mutableState = "";
    }

    public String getName() {
        return name;
    }

    public Set<Child> getChildren() {
        return unmodifiableSet(children);
    }

    public Child addChild(String name) {
        final Child child = new Child(this, name);
        if (children.add(child))
            return child;
        else
            return children.stream().filter(child::equals).findFirst().get();
    }

    public Child replaceChild(String initialChildName, String newChildName) {
        removeChild(initialChildName);
        return addChild(newChildName);
    }

    public void removeChild(String initialChildName) {
        children.removeIf(child -> child.getName().equals(initialChildName));
    }

    public Child replaceAllChildrenWith(String name) {
        children.clear();
        return addChild(name);
    }

    public String getMutableState() {
        return mutableState;
    }

    public void setMutableState(String mutableState) {
        this.mutableState = checkNotNull(mutableState);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass(this) != getClass(o)) return false;

        final Parent child = (Parent) o;

        return getName().equals(child.getName());
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final String toString() {
        return "Parent{" +
                "name='" + getName() + '\'' +
                "mutableState='" + getMutableState() + '\'' +
                ", children=" + getChildren() +
                '}';
    }

    // Solely for Hibernate
    protected Parent() {
    }
}

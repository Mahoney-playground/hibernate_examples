package hibernate_examples.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"NAME", "PARENT_ID"}))
public class Child extends hibernate_examples.hibernate.Entity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = LAZY, optional = false)
    private Parent parent;

    Child(Parent parent, String name) {
        this.parent = checkNotNull(parent);
        this.name = checkNotNull(name);
    }

    public String getName() {
        return name;
    }

    public Parent getParent() {
        return parent;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;

        final Child child = (Child) o;

        return  getName().equals(child.getName()) &&
                getParent().equals(child.getParent());
    }

    @Override
    public final int hashCode() {
        return hash(getName(), getParent());
    }

    @Override
    public final String toString() {
        return "Child{" +
                "name='" + getName() + '\'' +
                '}';
    }

    // Solely for Hibernate
    protected Child() {}
}

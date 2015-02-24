package hibernate_examples.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="PARENT", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class Parent {

    private Long id;
    private String name;
    private Set<Child> children = new HashSet<>();

    private Parent() {}

    public Parent(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Child> getChildren() {
        return children;
    }

    private void setChildren(Set<Child> children) {
        this.children = children;
    }

    public Child addChild(String name) {
        Child child = new Child(this, name);
        children.add(child);
        return child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parent child = (Parent) o;

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

    public void replaceChild(String name) {
        children.clear();
        addChild(name);
    }
}

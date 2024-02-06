package com.github.gluhov.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "writer")
public class Writer extends BaseEntity{
    @Column(name = "firstName", nullable = false)
    @NotBlank
    @Size(min = 2, max = 128)
    private String firstName;
    @Column(name = "lastName")
    private String lastName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "writer_post",
            joinColumns = { @JoinColumn(name = "writer_id") },
            inverseJoinColumns = { @JoinColumn(name = "post_id") }
    )
    private Set<Post> posts = new HashSet<>();

    public Writer(Long id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addPost(Post p) {
        this.posts.add(p);
        p.getWriters().add(this);
    }

    public void removePost(Post p) {
        this.posts.remove(p);
        p.getWriters().remove(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id: " + id + "; first name: " + firstName + "; last name: " + lastName + "; posts: [ ");
        if (Hibernate.isInitialized(posts)) {
            Iterator<Post> iterator = posts.iterator();
            while (iterator.hasNext()) {
                sb.append("[ ");
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append("] , ");
                }
            }
            sb.append(" ]");
        }
        sb.append(" ]");
        return sb.toString();
    }
}

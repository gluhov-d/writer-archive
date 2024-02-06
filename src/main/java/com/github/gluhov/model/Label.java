package com.github.gluhov.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "label")
public class Label extends BaseEntity{
    @NotBlank
    @Size(min = 2, max = 128)
    @Column(name = "name")
    private String name;

    @ToString.Exclude
    @ManyToMany(mappedBy = "labels", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @EqualsAndHashCode.Exclude
    private Set<Post> posts = new HashSet<>();

    public Label(long id, String name) {
        super(id);
        this.name = name;
    }

    public void addPost(Post p) {
        this.posts.add(p);
        p.getLabels().add(this);
    }

    public void removePost(Post p) {
        this.posts.remove(p);
        p.getLabels().remove(this);
    }

    @Override
    public String toString() {
        return "id: " + id + "; name: " + name + ";";
    }
}

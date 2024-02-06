package com.github.gluhov.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "post")
public class Post extends BaseEntity {
    @Column(name = "content",nullable = false)
    @NotBlank
    @Size(min = 2)
    private String content;
    @Column(name = "updated", nullable = false, columnDefinition = "timestamp default now()")
    @NotNull
    private LocalDateTime updated;
    @Column(name = "created", nullable = false, columnDefinition = "timestamp default now()", updatable = false)
    @NotNull
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PostStatus status;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "post_label",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "label_id") }
    )
    private Set<Label> labels = new HashSet<>();

    @ManyToMany(mappedBy = "posts", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Writer> writers = new HashSet<>();

    public Post(Long id, String content, PostStatus status) {
        super(id);
        this.content = content;
        this.status = status;
    }

    public void addWriter(Writer w) {
        this.getWriters().add(w);
        w.getPosts().add(this);
    }

    public void removeWriter(Writer w) {
        this.getWriters().remove(w);
        w.getPosts().remove(this);
    }

    public void addLabel(Label l) {
        this.labels.add(l);
        l.getPosts().add(this);
    }

    public void removeLabel(Label l) {
        this.labels.remove(l);
        l.getPosts().remove(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id: " + id + "; content: " + content + "; status: " + status + "; created: " + created + "; updated: " + updated +"; labels id: [ ");
        if (Hibernate.isInitialized(labels)) {
            Iterator<Label> iterator = labels.iterator();
            while (iterator.hasNext()) {

                sb.append("[ ");
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append(" ] , ");
                }
            }
            sb.append(" ]");
        }
        sb.append(" ]");
        return sb.toString();
    }
}

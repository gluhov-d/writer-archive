package com.github.gluhov.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "post")
public class Post extends BaseEntity {
    @Column(name = "content",nullable = false)
    @NotBlank
    @Size(min = 2)
    private String content;
    @Column(name = "updated", columnDefinition = "timestamp default now()")
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private LocalDateTime updated;
    @Column(name = "created", columnDefinition = "timestamp default now()", updatable = false)
    @Generated(event = {EventType.INSERT})
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PostStatus status;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "post_label",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "label_id") }
    )
    @EqualsAndHashCode.Exclude
    private Set<Label> labels = new HashSet<>();

    public Post(Long id, String content, PostStatus status) {
        super(id);
        this.content = content;
        this.status = status;
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

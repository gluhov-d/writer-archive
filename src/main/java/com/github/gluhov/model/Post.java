package com.github.gluhov.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Post extends BaseEntity {
    private String content;
    private LocalDateTime updated;
    private LocalDateTime created;
    private PostStatus status;
    private List<Label> labels;

    public Post(Long id, String content, PostStatus status) {
        super(id);
        this.content = content;
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id: " + id + "; content: " + content + "; status: " + status + "; created: " + created + "; updated: " + updated +"; labels id: [ ");
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++) {
                sb.append("[ ");
                sb.append(labels.get(i));
                if (i != labels.size() -1) {
                    sb.append(" ] , ");
                }
            }
            sb.append(" ]");
        }
        sb.append(" ]");
        return sb.toString();
    }
}

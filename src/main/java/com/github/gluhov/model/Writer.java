package com.github.gluhov.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class Writer extends BaseEntity{
    private String firstName;
    private String lastName;
    private List<Post> posts;

    public Writer(Long id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id: " + id + "; first name: " + firstName + "; last name: " + lastName + "; posts: [ ");
        if (posts != null) {
            for (int i = 0; i < posts.size(); i++) {
                sb.append(posts.get(i));
                if (i != posts.size()-1) {
                    sb.append(" , ");
                }
            }
        }
        sb.append(" ]");
        return sb.toString();
    }
}

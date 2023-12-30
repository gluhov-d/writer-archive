package com.github.gluhov.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class Label extends BaseEntity{
    private String name;

    public Label(long id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return "id: " + id + "; name: " + name + ";";
    }
}

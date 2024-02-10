package com.github.gluhov.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    public Label(long id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return "id: " + id + "; name: " + name + ";";
    }
}

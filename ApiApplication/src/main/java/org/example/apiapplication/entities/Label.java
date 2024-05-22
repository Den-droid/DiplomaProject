package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "labels")
@Data
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "labels")
    private List<Profile> profiles = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;
        return name.trim().equalsIgnoreCase(label.name.trim());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

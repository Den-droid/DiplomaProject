package org.example.apiapplication.entities.fields;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.extraction.FieldExtraction;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "fields")
@Data
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private FieldType type;

    @OneToMany(mappedBy = "field")
    private List<ProfileFieldValue> profileFieldValues;

    @OneToMany(mappedBy = "field")
    private List<FieldExtraction> fieldExtractions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;
        return id.equals(field.id) && name.equals(field.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}

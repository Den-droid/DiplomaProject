package org.example.apiapplication.entities.fields;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.enums.FieldTypeName;

@Entity
@Table(name = "field_types")
@Data
public class FieldType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FieldTypeName name;
}

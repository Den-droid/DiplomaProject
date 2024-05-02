package org.example.apiapplication.entities.fields;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.enums.FieldType;

@Entity
@Table(name = "fields")
@Data
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String key;
    private String rule;

    @Enumerated(EnumType.STRING)
    private FieldType type;

    @ManyToOne
    @JoinColumn(name = "rule_type_id", referencedColumnName = "id")
    private FieldRuleType ruleType;

    @ManyToOne
    @JoinColumn(name = "scientometric_system_id", referencedColumnName = "id")
    private ScientometricSystem scientometricSystem;
}

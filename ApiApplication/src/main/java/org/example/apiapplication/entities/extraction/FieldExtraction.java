package org.example.apiapplication.entities.extraction;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.FieldRuleType;

@Entity
@Table(name = "field_extraction_info")
@Data
public class FieldExtraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String key;
    private String rule;

    @ManyToOne
    @JoinColumn(name = "scientometric_system_id", referencedColumnName = "id")
    private ScientometricSystem scientometricSystem;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Field field;

    @ManyToOne
    @JoinColumn(name = "rule_type_id", referencedColumnName = "id")
    private FieldRuleType ruleType;

}

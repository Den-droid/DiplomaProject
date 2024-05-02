package org.example.apiapplication.entities.fields;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.enums.FieldRuleTypeName;

@Entity
@Table(name = "field_rule_types")
@Data
public class FieldRuleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FieldRuleTypeName name;
}

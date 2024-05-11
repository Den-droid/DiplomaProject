package org.example.apiapplication.entities.recommendation;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.fields.Field;

@Entity
@Table(name = "field_recommendations")
@Data
public class FieldRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String key;
    private String text;

    @ManyToOne
    @JoinColumn(name = "scientometric_system_id", referencedColumnName = "id")
    private ScientometricSystem scientometricSystem;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Field field;

    @ManyToOne
    @JoinColumn(name = "recommendation_type_id", referencedColumnName = "id")
    private RecommendationType recommendationType;
}

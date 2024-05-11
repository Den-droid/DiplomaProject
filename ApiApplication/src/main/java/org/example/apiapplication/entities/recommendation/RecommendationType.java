package org.example.apiapplication.entities.recommendation;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.enums.RecommendationTypeName;

@Entity
@Table(name = "recommendation_types")
@Data
public class RecommendationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RecommendationTypeName name;
}

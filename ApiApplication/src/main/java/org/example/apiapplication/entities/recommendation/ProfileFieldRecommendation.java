package org.example.apiapplication.entities.recommendation;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.fields.ProfileFieldValue;

@Entity
@Table(name = "profile_field_recommendations")
@Data
public class ProfileFieldRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private boolean isConditionFulfilled;

    @ManyToOne
    @JoinColumn(name = "profile_field_value_id", referencedColumnName = "id")
    private ProfileFieldValue profileFieldValue;
}

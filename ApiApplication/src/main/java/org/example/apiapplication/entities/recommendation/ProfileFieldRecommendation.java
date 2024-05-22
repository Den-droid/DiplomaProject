package org.example.apiapplication.entities.recommendation;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.fields.Field;

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
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Field field;
}

package org.example.apiapplication.entities.fields;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.recommendation.ProfileFieldRecommendation;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profile_field_values")
@Data
public class ProfileFieldValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String key;
    private String value;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Field field;

    @OneToMany(mappedBy = "profileFieldValue")
    private List<ProfileFieldRecommendation> profileFieldRecommendations = new ArrayList<>();
}

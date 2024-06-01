package org.example.apiapplication.entities.fields;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.Profile;

@Entity
@Table(name = "profile_field_values")
@Data
public class ProfileFieldValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String value;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Field field;
}

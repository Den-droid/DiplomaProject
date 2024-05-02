package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.fields.ProfileFieldValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Data
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "are_works_doubtful")
    private boolean areWorksDoubtful;

    @Column(name = "is_deactivated")
    private boolean isDeactivated;

    @Column(name = "profile_user_id")
    private String profileUserId;

    @ManyToOne
    @JoinColumn(name = "scientometric_system_id", referencedColumnName = "id")
    private ScientometricSystem scientometricSystem;

    @ManyToOne
    @JoinColumn(name = "scientist_id", referencedColumnName = "id")
    private Scientist scientist;

    @ManyToMany
    @JoinTable(
            name = "profile_labels",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new HashSet<>();

    @OneToMany(mappedBy = "profile")
    private List<ProfileFieldValue> profileFieldValues = new ArrayList<>();
}

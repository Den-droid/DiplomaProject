package org.example.apiapplication.entities.extraction;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.Profile;

@Entity
@Table(name = "extraction_profiles")
@Data
public class ExtractionProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_finished")
    private boolean isFinished;

    @ManyToOne
    @JoinColumn(name = "extraction_id", referencedColumnName = "id")
    private Extraction extraction;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;
}

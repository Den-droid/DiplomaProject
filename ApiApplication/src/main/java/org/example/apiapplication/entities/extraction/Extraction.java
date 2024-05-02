package org.example.apiapplication.entities.extraction;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.ScientometricSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "extractions")
@Data
public class Extraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_started")
    private LocalDate dateStarted;

    @Column(name = "is_finished")
    private boolean isFinished;

    @ManyToOne
    @JoinColumn(name = "scientometric_system_id", referencedColumnName = "id")
    private ScientometricSystem scientometricSystem;

    @OneToMany(mappedBy = "extraction")
    private List<ExtractionProfile> extractedProfiles = new ArrayList<>();
}

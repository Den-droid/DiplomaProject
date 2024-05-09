package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.extraction.Extraction;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.enums.ScientometricSystemName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scientometric_systems")
@Data
public class ScientometricSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ScientometricSystemName name;

    @Column(name = "next_min_import_date")
    private LocalDate nextMinImportDate;

    @Column(name = "profile_import_periodicity")
    private Integer profileImportPeriodicity;

    @OneToMany(mappedBy = "scientometricSystem")
    private List<Extraction> extractions = new ArrayList<>();

    @OneToMany(mappedBy = "scientometricSystem")
    private List<Profile> profiles = new ArrayList<>();
}

package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faculties")
@Data
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "english_abbreviation")
    private String englishAbbreviation;

    @Column(name = "ukrainian_name")
    private String ukrainianName;

    @Column(name = "ukrainian_abbreviation")
    private String ukrainianAbbreviation;

    @OneToMany(mappedBy = "faculty")
    private List<Scientist> scientists = new ArrayList<>();

    @OneToMany(mappedBy = "faculty")
    private List<Chair> chairs = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Faculty faculty = (Faculty) o;
        return id.equals(faculty.id)
                && englishName.equals(faculty.englishName)
                && englishAbbreviation.equals(faculty.englishAbbreviation)
                && ukrainianName.equals(faculty.ukrainianName)
                && ukrainianAbbreviation.equals(faculty.ukrainianAbbreviation);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + englishName.hashCode();
        result = 31 * result + englishAbbreviation.hashCode();
        result = 31 * result + ukrainianName.hashCode();
        result = 31 * result + ukrainianAbbreviation.hashCode();
        return result;
    }
}

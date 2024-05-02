package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chairs")
@Data
public class Chair {
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

    @ManyToOne
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private Faculty faculty;

    @OneToMany(mappedBy = "chair")
    private List<Scientist> scientists = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chair chair = (Chair) o;
        return id.equals(chair.id)
                && englishName.equals(chair.englishName)
                && englishAbbreviation.equals(chair.englishAbbreviation)
                && ukrainianName.equals(chair.ukrainianName)
                && ukrainianAbbreviation.equals(chair.ukrainianAbbreviation)
                && faculty.equals(chair.faculty);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + englishName.hashCode();
        result = 31 * result + englishAbbreviation.hashCode();
        result = 31 * result + ukrainianName.hashCode();
        result = 31 * result + ukrainianAbbreviation.hashCode();
        result = 31 * result + faculty.hashCode();
        return result;
    }
}

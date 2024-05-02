package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scientists")
@Data
public class Scientist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;
    private String position;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chair_id", referencedColumnName = "id")
    private Chair chair;

    @ManyToOne
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private Faculty faculty;

    @OneToMany(mappedBy = "scientist")
    private List<Profile> profiles = new ArrayList<>();
}

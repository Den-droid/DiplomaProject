package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.ScientometricSystem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Integer> {
    List<Profile> findByScientometricSystem(ScientometricSystem scientometricSystem);

    Optional<Profile> findByScientometricSystemAndScientist(ScientometricSystem scientometricSystem,
                                                            Scientist scientist);

    List<Profile> findAllByScientometricSystemAndScientistIn(ScientometricSystem scientometricSystem,
                                                             List<Scientist> scientist);

    List<Profile> findAllByScientometricSystemAndScientistInAndAreWorksDoubtful(
            ScientometricSystem scientometricSystem, List<Scientist> scientist,
            boolean areWorksDoubtful);

    List<Profile> findAllByScientometricSystemAndScientistInAndAreWorksDoubtfulAndIsActive(
            ScientometricSystem scientometricSystem, List<Scientist> scientist,
            boolean areWorksDoubtful, boolean active);
}

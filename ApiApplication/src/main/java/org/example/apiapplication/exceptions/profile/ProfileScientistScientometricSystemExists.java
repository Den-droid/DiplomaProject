package org.example.apiapplication.exceptions.profile;

public class ProfileScientistScientometricSystemExists extends RuntimeException {
    public ProfileScientistScientometricSystemExists() {
        super("Профіль для наукометричної БД вже існує!");
    }
}

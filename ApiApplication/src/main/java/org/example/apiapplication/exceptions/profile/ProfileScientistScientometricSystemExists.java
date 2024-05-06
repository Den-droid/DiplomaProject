package org.example.apiapplication.exceptions.profile;

public class ProfileScientistScientometricSystemExists extends RuntimeException{
    public ProfileScientistScientometricSystemExists() {
        super("Profile for scientist system already exists");
    }
}

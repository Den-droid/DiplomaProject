package org.example.apiapplication.exceptions.extraction;

public class ProfileByProfileIdNotFoundException extends Exception {
    public ProfileByProfileIdNotFoundException(String profileId) {
        super("Profile by profile id " + profileId + " not found");
    }
}

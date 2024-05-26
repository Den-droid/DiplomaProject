package org.example.apiapplication.exceptions.extraction;

public class ProfileByProfileIdNotFoundException extends Exception {
    public ProfileByProfileIdNotFoundException(String profileId) {
        super("Профіль по ідентифікатору профіля " + profileId + " не знайдено!");
    }
}

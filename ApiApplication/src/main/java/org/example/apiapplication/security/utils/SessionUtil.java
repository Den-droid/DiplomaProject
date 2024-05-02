package org.example.apiapplication.security.utils;

import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.exceptions.auth.UserWithUsernameNotExistsException;
import org.example.apiapplication.repositories.UserRepository;
import org.example.apiapplication.security.user_details.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {
    private final UserRepository userRepository;

    public SessionUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserFromSession() {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserWithUsernameNotExistsException(username));
    }

}

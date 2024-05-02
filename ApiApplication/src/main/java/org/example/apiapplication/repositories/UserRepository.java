package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByForgotPasswordToken(String forgotPasswordToken);

    Optional<User> findByInviteCode(String inviteCode);

    boolean existsByForgotPasswordToken(String token);
    boolean existsByInviteCode(String inviteCode);

}

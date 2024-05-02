package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.auth.*;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.auth.*;
import org.example.apiapplication.exceptions.scientist.ScientistWithIdNotExistsException;
import org.example.apiapplication.exceptions.user.RoleNotFoundException;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.repositories.ScientistRepository;
import org.example.apiapplication.repositories.UserRepository;
import org.example.apiapplication.security.jwt.JwtUtils;
import org.example.apiapplication.services.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ScientistRepository scientistRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           ScientistRepository scientistRepository,
                           RoleRepository roleRepository,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.scientistRepository = scientistRepository;
        this.roleRepository = roleRepository;

        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RoleTokensDto signIn(SignInDto signInDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInDto.email(), signInDto.password()));

        User user = userRepository.findByUsername(signInDto.email()).get();

        if (!user.isApproved()) {
            throw new UserNotApprovedException();
        }

        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateAccessToken(signInDto.email());
        String refreshToken = jwtUtils.generateRefreshToken(signInDto.email());

        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return new RoleTokensDto(user.getRoles().stream()
                .map((x) -> x.getName().name())
                .toList(), new TokensDto(accessToken, refreshToken));
    }

    @Override
    public TokensDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String requestRefreshToken = refreshTokenDto.refreshToken();

        if (!jwtUtils.validateRefreshToken(requestRefreshToken)) {
            throw new TokenRefreshException(requestRefreshToken, "Refresh token is invalid! " +
                    "Please sign in again to get new!");
        }

        String username = jwtUtils.getUserNameFromRefreshToken(requestRefreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));

        String accessToken = jwtUtils.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new TokensDto(accessToken, refreshToken);
    }

    @Override
    public void signUp(SignUpDto signUpDto) {
        Optional<User> optionalUser = userRepository.findByUsername(signUpDto.email());
        if (optionalUser.isPresent()) {
            throw new UserWithUsernameExistsException(signUpDto.email());
        }

        User user = new User();
        user.setUsername(signUpDto.email());
        user.setEmail(signUpDto.email());
        user.setFullName(signUpDto.fullName());
        user.setPassword(passwordEncoder.encode(signUpDto.password()));
        user.setApproved(false);
        user.setActive(false);

        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new RoleNotFoundException(UserRole.USER.name()));
        roles.add(userRole);
        user.setRoles(roles);

        Scientist scientist = scientistRepository
                .findById(signUpDto.scientistId())
                .orElseThrow(() -> new ScientistWithIdNotExistsException(signUpDto.scientistId()));
        scientist.setUser(user);

        scientistRepository.save(scientist);
        userRepository.save(user);
    }

    @Override
    public void signUpAdminByInviteCode(String inviteCode, AdminSignUpDto adminSignUpDto) {
        User user = userRepository.findByInviteCode(inviteCode)
                .orElseThrow(UserWithInviteCodeNotExistsException::new);

        user.setApproved(true);
        user.setActive(true);
        user.setFullName(adminSignUpDto.fullName());
        user.setPassword(passwordEncoder.encode(adminSignUpDto.password()));
        user.setInviteCode(null);

        userRepository.save(user);
    }

    @Override
    public List<SignUpScientistDto> getSignUpPageDto() {
        List<Scientist> scientists = scientistRepository.findAllByUserNull();
        return scientists.stream()
                .map((x) -> new SignUpScientistDto(x.getId(), x.getFullName()))
                .toList();
    }

    @Override
    public void changeForgotPassword(String token, ChangePasswordDto changePasswordDto) {
        User user = userRepository
                .findByForgotPasswordToken(token)
                .orElseThrow(UserWithTokenNotExistsException::new);

        user.setForgotPasswordToken(null);
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));

        userRepository.save(user);
    }

    @Override
    public void createForgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository
                .findByUsername(forgotPasswordDto.email())
                .orElseThrow(() -> new UserWithUsernameNotExistsException(forgotPasswordDto.email()));

        user.setForgotPasswordToken(UUID.randomUUID().toString());

        // send email

        userRepository.save(user);
    }

    @Override
    public boolean existsForgotPasswordToken(String token) {
        return userRepository.existsByForgotPasswordToken(token);
    }
}

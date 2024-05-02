package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.auth.*;
import org.example.apiapplication.services.interfaces.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> authenticateUser(@RequestBody SignInDto signInDto) {
        RoleTokensDto jwtDto = authService.signIn(signInDto);
        return ResponseEntity.ok(jwtDto);
    }

    @PutMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        TokensDto tokensDto =
                authService.refreshToken(refreshTokenDto);
        return ResponseEntity.ok(tokensDto);
    }

    @GetMapping("/signUp")
    public ResponseEntity<?> getSignUpPage() {
        List<SignUpScientistDto> scientists = authService.getSignUpPageDto();
        return ResponseEntity.ok(scientists);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/signUp/{inviteCode}")
    public ResponseEntity<?> adminSignUp(@PathVariable String inviteCode,
                                         @RequestBody AdminSignUpDto adminSignUpDto) {
        authService.signUpAdminByInviteCode(inviteCode, adminSignUpDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgotPassword/tokenExists/{token}")
    public ResponseEntity<?> existsForgotPasswordToken(@PathVariable String token) {
        boolean tokenExists = authService.existsForgotPasswordToken(token);
        return ResponseEntity.ok(tokenExists);
    }

    @GetMapping("/signUp/existsByInviteCode/{inviteCode}")
    public ResponseEntity<?> existsByInviteCode(@PathVariable String inviteCode) {
        boolean codeExists = authService.existsInviteCode(inviteCode);
        return ResponseEntity.ok(codeExists);
    }

    @PostMapping("/forgotPassword/create")
    public ResponseEntity<?> createForgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        authService.createForgotPassword(forgotPasswordDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgotPassword/change/{token}")
    public ResponseEntity<?> changeForgotPassword(@PathVariable String token,
                                                  @RequestBody ChangePasswordDto changePasswordDto) {
        authService.changeForgotPassword(token, changePasswordDto);
        return ResponseEntity.ok().build();
    }
}

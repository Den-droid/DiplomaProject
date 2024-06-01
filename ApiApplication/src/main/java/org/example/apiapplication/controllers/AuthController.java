package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.auth.*;
import org.example.apiapplication.services.interfaces.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {
        TokensDto jwtDto = authService.signIn(signInDto);
        return ResponseEntity.ok(jwtDto);
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        TokensDto tokensDto =
                authService.refreshToken(refreshTokenDto);
        return ResponseEntity.ok(tokensDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sign-up/{inviteCode}")
    public ResponseEntity<?> adminSignUp(@PathVariable String inviteCode,
                                         @RequestBody AdminSignUpDto adminSignUpDto) {
        authService.signUpAdminByInviteCode(inviteCode, adminSignUpDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgot-password/token-exists")
    public ResponseEntity<?> existsForgotPasswordToken(@RequestParam String token) {
        boolean tokenExists = authService.existsForgotPasswordToken(token);
        return ResponseEntity.ok(tokenExists);
    }

    @GetMapping("/sign-up/invite-code-exists")
    public ResponseEntity<?> existsInviteCode(@RequestParam String inviteCode) {
        boolean codeExists = authService.existsInviteCode(inviteCode);
        return ResponseEntity.ok(codeExists);
    }

    @PostMapping("/forgot-password/create")
    public ResponseEntity<?> createForgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        authService.createForgotPassword(forgotPasswordDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password/change/{token}")
    public ResponseEntity<?> changeForgotPassword(@PathVariable String token,
                                                  @RequestBody ChangePasswordDto changePasswordDto) {
        authService.changeForgotPassword(token, changePasswordDto);
        return ResponseEntity.ok().build();
    }
}

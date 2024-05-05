package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.auth.*;

import java.util.List;

public interface AuthService {
    void signUp(SignUpDto signUpDto);

    void signUpAdminByInviteCode(String inviteCode, AdminSignUpDto adminSignUpDto);

    TokensDto signIn(SignInDto signInDto);

    List<SignUpScientistDto> getSignUpPageDto();

    TokensDto refreshToken(RefreshTokenDto refreshTokenDt);

    void changeForgotPassword(String token, ChangePasswordDto changePasswordDto);

    void createForgotPassword(ForgotPasswordDto forgotPasswordDto);

    boolean existsForgotPasswordToken(String token);

    boolean existsInviteCode(String inviteCode);

}

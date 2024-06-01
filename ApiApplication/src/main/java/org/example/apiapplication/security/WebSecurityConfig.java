package org.example.apiapplication.security;

import org.example.apiapplication.security.jwt.AuthEntryPointJwt;
import org.example.apiapplication.security.jwt.AuthTokenFilter;
import org.example.apiapplication.security.jwt.JwtUtils;
import org.example.apiapplication.security.user_details.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    private final String[] authUrl = {
            "/api/auth/sign-in",
            "/api/auth/refresh-token",
            "/api/auth/sign-up",
            "/api/auth/sign-up/{inviteCode}",
            "/api/auth/forgot-password/token-exists",
            "/api/auth/sign-up/exists-by-invite-code",
            "/api/auth/forgot-password/create",
            "/api/auth/forgot-password/change/{token}"
    };
    private final String[] chairsUrls = {
            "/api/chairs",
            "/api/accessible-for-current-user",
    };
    private final String[] facultiesUrls = {
            "/api/faculties",
            "/api/faculties/indices",
            "/api/faculties/{id}/indices",
            "/api/faculties/accessible-for-current-user"
    };

    private final String[] fieldsUrls = {
            "/api/fields/types",
            "/api/fields/{id}/delete",
            "/api/fields",
            "/api/fields/{id}",
            "/api/fields/search"
    };

    private final String[] labelsUrls = {
            "/api/labels",
            "/api/labels/{id}",
            "/api/labels/search",
            "/api/labels/{id}/delete"
    };

    private final String[] scientometricSystemsUrls = {
            "/api/scientometric-systems",
            "/api/scientometric-systems/{id}/extraction/is-running",
            "/api/scientometric-systems/{id}/extraction/is-possible",
            "/api/scientometric-systems/{id}/extraction/errors"
    };

    private final String[] scientistsUrls = {
            "/api/scientists/not-registered",
            "/api/scientists/accessible-for-current-user"};

    private final String[] permissionsUrls = {"/api/permissions"};
    private final String[] rolesUrls = {
            "/api/roles",
            "/api/roles/{id}/possible-permissions",
            "/api/roles/{id}/default-permissions",
            "/api/roles/update-default-permissions"
    };

    private final String[] profilesUrls = {
            "/api/profiles",
            "/api/profiles/{id}",
            "/api/profiles/search",
            "/api/profiles/common-labels",
            "/api/profiles/{id}/labels",
            "/api/profiles/{id}/fields",
            "/api/profiles/{id}/mark-doubtful",
            "/api/profiles/{id}/unmark-doubtful",
            "/api/profiles/{id}/activate",
            "/api/profiles/{id}/deactivate",
            "/api/profiles/can-add-profile",
            "/api/profiles/accessible-for-current-user",
            "/api/profiles/accessible-for-current-user/search"
    };

    private final String[] usersUrls = {
            "/api/users",
            "/api/users/{id}",
            "/api/users/{id}/activate",
            "/api/users/{id}/deactivate",
            "/api/users/{id}/approve",
            "/api/users/{id}/reject",
            "/api/users/search",
            "/api/users/admins",
            "/api/users/admins/{id}",
            "/api/users/{id}/editDto",
            "/api/users/{id}/roles",
            "/api/users/{id}/permissions",
            "/api/users/current",
            "/api/users/current/chairs",
            "/api/users/current/faculties",
            "/api/users/current/scientists",
            "/api/users/current/permissions",
            "/api/users/current/canEditUser",
            "/api/users/current/canEditProfile"
    };

    private final String[] extractionUrls = {"/api/extraction/scholar"};

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                             AuthEntryPointJwt unauthorizedHandler,
                             JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(authUrl).permitAll()
                                .requestMatchers(chairsUrls).permitAll()
                                .requestMatchers(facultiesUrls).permitAll()
                                .requestMatchers(fieldsUrls).permitAll()
                                .requestMatchers(labelsUrls).permitAll()
                                .requestMatchers(scientometricSystemsUrls).permitAll()
                                .requestMatchers(scientistsUrls).permitAll()
                                .requestMatchers(permissionsUrls).permitAll()
                                .requestMatchers(rolesUrls).permitAll()
                                .requestMatchers(profilesUrls).permitAll()
                                .requestMatchers(usersUrls).permitAll()
                                .requestMatchers(extractionUrls).permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

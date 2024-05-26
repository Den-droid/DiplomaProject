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

    private final String[] sharedUrls = {
            "/api/auth/**",
            "/api/chairs",
            "/api/chairs/{id}",
            "/api/faculties",
            "/api/faculties/{id}",
            "/api/faculties/indices",
            "/api/faculties/{id}/indices",
            "/api/fields",
            "/api/fields/types",
            "/api/fields/{id}",
            "/api/fields/search",
            "/api/labels",
            "/api/labels/{id}",
            "/api/labels/search",
            "/api/scientists/notRegistered",
            "/api/scientometricSystems",
            "/api/profiles/forUser",
            "/api/profiles/commonLabels"
    };

    private final String[] mainAdminUrls = {
            "/api/fields/delete/{id}",
            "/api/labels/delete/{id}",
            "/api/roles/updateDefaultPermissions",
            "/api/scientometricSystems/{id}/extraction/isRunning",
            "/api/scientometricSystems/{id}/extraction/isPossible",
            "/api/scientometricSystems/{id}/extraction/errors",
            "/api/profiles/{id}/markDoubtful",
            "/api/profiles/{id}/unmarkDoubtful"
    };

    private final String[] facultyChairMainAdminUrls = {
            "/api/permissions",
            "/api/roles/",
            "/api/roles/{id}/possiblePermissions",
            "/api/roles/{id}/defaultPermissions",
            "/api/profiles/{id}/activate",
            "/api/profiles/{id}/deactivate",
    };

    private final String[] userUrls = {
            "/api/users/current/chairs",
            "/api/users/current/faculties",
            "/api/users/current/scientists",
            "/api/profiles/canAddProfile",
            "/api/users/current"
    };

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
                                auth.requestMatchers(sharedUrls).permitAll()
                                        .requestMatchers(mainAdminUrls).permitAll()
                                        .requestMatchers(facultyChairMainAdminUrls).permitAll()
                                        .requestMatchers(userUrls).permitAll()
//                                        .requestMatchers(mainAdminUrls).hasAuthority("ROLE_" + UserRole.MAIN_ADMIN.name())
//                                .requestMatchers(facultyChairAdminUrls).hasAnyAuthority("ROLE_" + UserRole.FACULTY_ADMIN.name(),
//                                        "ROLE_" + UserRole.CHAIR_ADMIN.name(), "ROLE_" + UserRole.MAIN_ADMIN.name()")
//                                .requestMatchers(userUrls).hasAnyAuthority("ROLE_" + UserRole.USER.name(),
//                                "ROLE_" + UserRole.FACULTY_ADMIN.name(), "ROLE_" + UserRole.CHAIR_ADMIN.name(),
////                                        "ROLE_" + UserRole.MAIN_ADMIN.name()")
                                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

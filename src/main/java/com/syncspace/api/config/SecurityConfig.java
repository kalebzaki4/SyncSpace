package com.syncspace.api.config;

import com.syncspace.api.security.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/medicos/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/medicos/**").hasAnyRole("ADMIN", "MEDICO")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/medicos/**").hasAnyRole("ADMIN", "MEDICO")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/medicos/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/pacientes").hasAnyRole("ADMIN", "ATENDENTE", "MEDICO")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/pacientes/**").hasAnyRole("ADMIN", "ATENDENTE", "MEDICO", "PACIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/pacientes/**").hasAnyRole("ADMIN", "PACIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/pacientes/**").hasAnyRole("ADMIN", "PACIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/atendentes/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/atendentes/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/atendentes/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/atendentes/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
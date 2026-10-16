package com.syncspace.api.config;

import com.syncspace.api.security.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        .requestMatchers("/salas", "/salas/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.GET, "/medicos", "/medicos/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.POST, "/medicos", "/medicos/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.PUT, "/medicos", "/medicos/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.DELETE, "/medicos", "/medicos/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.GET, "/pacientes", "/pacientes/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.POST, "/pacientes", "/pacientes/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.PUT, "/pacientes", "/pacientes/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers("/pacientes", "/pacientes/**").denyAll()
                        .requestMatchers(HttpMethod.GET, "/consultas/minhas-consultas").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.POST, "/consultas/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.PUT, "/consultas/**").hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers("/consultas/**").denyAll()
                        .requestMatchers("/atendentes/**").hasAnyRole("ADMIN", "ATENDENTE")
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
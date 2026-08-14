package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.reserva.Reserva;
import com.br.syncspace.infra.exception.SenhaInvalidaException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String telefone;

    @JoinColumn(name = "reserva_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Reserva reserva;

    public static void validarFormatacaoSenha(String rawPassword) {
        if (rawPassword == null || !rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")) {
            throw new SenhaInvalidaException("A senha deve ter no mínimo 8 caracteres e conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.");
        }
    }

    public void validarEmail() {
        if (this.email == null || !this.email.contains("@") || !this.email.contains(".")) {
            throw new IllegalArgumentException("O formato do e-mail informado é inválido.");
        }
    }

    public void setEmail(String email) {
        this.email = email;
        validarEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
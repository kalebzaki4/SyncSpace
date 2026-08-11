package com.br.syncspace.infra.config;

import com.br.syncspace.domain.usuario.UserRole;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.password.encoder.secret}")
    String senhaCriptografada;

    Logger log = Logger.getLogger(DatabaseSeeder.class.getName());

    @Autowired
    public DatabaseSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("admin@syncspace.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador Master");
            admin.setEmail("admin@syncspace.com");
            admin.setPassword(passwordEncoder.encode(senhaCriptografada));
            admin.setRole(UserRole.ADMIN);

            usuarioRepository.save(admin);
            log.info("[syncspace] Primeiro Administrador criado com sucesso!\"");
        }
    }
}
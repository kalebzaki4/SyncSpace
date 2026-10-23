package com.syncspace.api;

import com.syncspace.api.enums.Setores;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.model.Atendente;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.AtendenteRepository;
import com.syncspace.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;

@EnableScheduling
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	@Profile("local")
	ApplicationRunner bootstrapLocalAtendente(
			UsuarioRepository usuarioRepository,
			AtendenteRepository atendenteRepository,
			PasswordEncoder passwordEncoder,
			@Value("${syncspace.bootstrap.atendente.email}") String email,
			@Value("${syncspace.bootstrap.atendente.senha}") String senha
	) {
		return args -> {
			Usuario usuario = usuarioRepository.findByEmail(email);
			if (usuario == null) {
				usuario = usuarioRepository.save(Usuario.builder()
						.nome("Atendente de Teste")
						.email(email)
						.senha(passwordEncoder.encode(senha))
						.telefone("11999999999")
						.cpf("00000000001")
						.dataNascimento(Date.from(Instant.parse("1990-01-01T00:00:00Z")))
						.role(UserRole.ATENDENTE)
						.build());
			} else if (usuario.getRole() != UserRole.ATENDENTE) {
				throw new IllegalStateException("O email configurado para bootstrap já pertence a outro perfil.");
			}

			if (!atendenteRepository.existsByUsuario_Id(usuario.getId())) {
				atendenteRepository.save(Atendente.builder()
						.usuario(usuario)
						.setor(Setores.ATENDIMENTO)
						.build());
			}
		};
	}

	@Bean
	@Profile("local")
	ApplicationRunner bootstrapLocalAdmin(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			@Value("${syncspace.bootstrap.admin.email:}") String email,
			@Value("${syncspace.bootstrap.admin.senha:}") String senha
	) {
		return args -> {
			if (email.isBlank() && senha.isBlank()) {
				return;
			}
			if (email.isBlank() || senha.isBlank()) {
				throw new IllegalStateException(
						"As variáveis SYNCSPACE_BOOTSTRAP_ADMIN_EMAIL e SYNCSPACE_BOOTSTRAP_ADMIN_SENHA devem ser configuradas juntas."
				);
			}

			Usuario usuario = usuarioRepository.findByEmail(email);
			if (usuario == null) {
				usuarioRepository.save(Usuario.builder()
						.nome("Administrador SyncSpace")
						.email(email)
						.senha(passwordEncoder.encode(senha))
						.telefone("11999999998")
						.cpf("00000000002")
						.dataNascimento(Date.from(Instant.parse("1990-01-01T00:00:00Z")))
						.role(UserRole.ADMIN)
						.build());
			} else if (usuario.getRole() != UserRole.ADMIN) {
				throw new IllegalStateException("O email configurado para bootstrap já pertence a outro perfil.");
			}
		};
	}

}

package com.net.convertix.ramossomar.config;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	@Bean
	CommandLineRunner inicializarAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!usuarioRepository.existsByEmail("admin@ramossomar.com")) {
				Usuario admin = new Usuario();
				admin.setNome("Administrador");
				admin.setEmail("admin@ramossomar.com");
				admin.setSenha(passwordEncoder.encode("admin123"));
				admin.setPerfil(Perfil.ADMIN);
				admin.setTelefone("41999999999");
				admin.setAtivo(true);
				usuarioRepository.save(admin);
				log.info("Usuário admin padrão criado: admin@ramossomar.com / admin123");
			}
		};
	}
}

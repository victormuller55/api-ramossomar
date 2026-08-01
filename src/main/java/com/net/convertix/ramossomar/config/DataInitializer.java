package com.net.convertix.ramossomar.config;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.service.CidadeLocalVotacaoImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(ImportacaoProperties.class)
public class DataInitializer {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	/**
	 * Hibernate ddl-auto=update nem sempre remove NOT NULL legado.
	 * Garante que CPF possa ser nulo conforme a regra de negócio atual.
	 */
	@Bean
	@Order(0)
	CommandLineRunner tornarCpfApoiadorOpcional(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				String isNullable = jdbcTemplate.query(
						"""
						SELECT IS_NULLABLE
						FROM INFORMATION_SCHEMA.COLUMNS
						WHERE TABLE_SCHEMA = DATABASE()
						  AND TABLE_NAME = 'tbl_apoiadores'
						  AND COLUMN_NAME = 'cpf'
						""",
						rs -> rs.next() ? rs.getString(1) : null
				);

				if ("NO".equalsIgnoreCase(isNullable)) {
					jdbcTemplate.execute("ALTER TABLE tbl_apoiadores MODIFY COLUMN cpf VARCHAR(14) NULL");
					log.info("Coluna tbl_apoiadores.cpf ajustada para aceitar NULL");
				}
			} catch (Exception ex) {
				log.warn("Não foi possível ajustar a coluna cpf para opcional: {}", ex.getMessage());
			}
		};
	}

	@Bean
	@Order(1)
	CommandLineRunner inicializarAdmin(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			@Value("${app.admin.auto-create:false}") boolean autoCreate
	) {
		return args -> {
			if (!autoCreate) {
				return;
			}
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

	@Bean
	@Order(2)
	CommandLineRunner inicializarCidadesELocaisVotacao(CidadeLocalVotacaoImportService importService) {
		return args -> {
			try {
				importService.importarCidadesSeNecessario();
			} catch (Exception ex) {
				log.error("Falha na importação automática de cidades: {}", ex.getMessage(), ex);
				return;
			}

			try {
				importService.importarLocaisVotacaoSeNecessario();
			} catch (Exception ex) {
				log.error("Falha na importação automática de locais de votação: {}", ex.getMessage(), ex);
			}
		};
	}
}

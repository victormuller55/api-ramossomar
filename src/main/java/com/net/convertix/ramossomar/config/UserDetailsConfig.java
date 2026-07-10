package com.net.convertix.ramossomar.config;

import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.security.UsuarioAutenticado;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserDetailsConfig {

	@Bean
	public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
		return email -> usuarioRepository.findByEmail(email)
				.map(UsuarioAutenticado::new)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
	}
}

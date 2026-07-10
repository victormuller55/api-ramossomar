package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.LoginRequest;
import com.net.convertix.ramossomar.dto.response.LoginResponse;
import com.net.convertix.ramossomar.exception.NegocioException;
import com.net.convertix.ramossomar.model.TokenRefresh;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.repository.TokenRefreshRepository;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final TokenRefreshRepository tokenRefreshRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthService(
			UsuarioRepository usuarioRepository,
			TokenRefreshRepository tokenRefreshRepository,
			PasswordEncoder passwordEncoder,
			JwtUtil jwtUtil
	) {
		this.usuarioRepository = usuarioRepository;
		this.tokenRefreshRepository = tokenRefreshRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@Transactional
	public LoginResponse login(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

		if (!Boolean.TRUE.equals(usuario.getAtivo())) {
			throw new NegocioException("Usuário inativo");
		}

		if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
			throw new BadCredentialsException("E-mail ou senha inválidos");
		}

		LocalDateTime expiraEm = jwtUtil.obterExpiracaoFimDoDia();
		String accessToken = jwtUtil.gerarToken(usuario);
		String refreshToken = UUID.randomUUID().toString();

		TokenRefresh tokenRefresh = new TokenRefresh();
		tokenRefresh.setUsuario(usuario);
		tokenRefresh.setToken(refreshToken);
		tokenRefresh.setExpiraEm(expiraEm);
		tokenRefreshRepository.save(tokenRefresh);

		usuario.setDataUltimoLogin(LocalDateTime.now());
		usuarioRepository.save(usuario);

		LoginResponse response = new LoginResponse();
		response.setAccess_token(accessToken);
		response.setRefresh_token(refreshToken);
		response.setTipo_token("Bearer");
		response.setExpira_em(expiraEm);
		response.setId_usuario(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setPerfil(usuario.getPerfil());
		response.setTelefone(usuario.getTelefone());
		response.setImagem(usuario.getImagem());
		return response;
	}
}

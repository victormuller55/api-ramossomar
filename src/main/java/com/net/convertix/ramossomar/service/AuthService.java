package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.LoginRequest;
import com.net.convertix.ramossomar.dto.response.LoginResponse;
import com.net.convertix.ramossomar.model.TokenRefresh;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.repository.TokenRefreshRepository;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private static final String CREDENCIAIS_INVALIDAS = "Usuário ou senha inválidos.";

	private final UsuarioRepository usuarioRepository;
	private final TokenRefreshRepository tokenRefreshRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	private String dummyPasswordHash;

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

	@PostConstruct
	void initTimingSafeHash() {
		this.dummyPasswordHash = passwordEncoder.encode("timing-safe-dummy");
	}

	@Transactional
	public LoginResponse login(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

		// Mitiga timing attack: sempre executa matches, mesmo se o e-mail não existir
		String hash = usuario != null ? usuario.getSenha() : dummyPasswordHash;
		boolean senhaOk = passwordEncoder.matches(request.getSenha(), hash);

		if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo()) || !senhaOk) {
			throw new BadCredentialsException(CREDENCIAIS_INVALIDAS);
		}

		LocalDateTime expiraEm = jwtUtil.obterExpiracao();
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

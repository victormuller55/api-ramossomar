package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.TokenRefreshRequest;
import com.net.convertix.ramossomar.dto.request.TokenRefreshUpdateRequest;
import com.net.convertix.ramossomar.dto.response.TokenRefreshResponse;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.TokenRefresh;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.repository.TokenRefreshRepository;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.security.UsuarioAutenticado;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenRefreshService {

	private final TokenRefreshRepository tokenRefreshRepository;
	private final UsuarioRepository usuarioRepository;
	private final SegurancaUtil segurancaUtil;

	public TokenRefreshService(
			TokenRefreshRepository tokenRefreshRepository,
			UsuarioRepository usuarioRepository,
			SegurancaUtil segurancaUtil
	) {
		this.tokenRefreshRepository = tokenRefreshRepository;
		this.usuarioRepository = usuarioRepository;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional
	public TokenRefreshResponse criar(TokenRefreshRequest request) {
		segurancaUtil.exigirAdminOuProprioUsuario(request.getId_usuario());

		Usuario usuario = usuarioRepository.findById(request.getId_usuario())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

		TokenRefresh token = new TokenRefresh();
		token.setUsuario(usuario);
		token.setToken(request.getToken());
		token.setExpiraEm(request.getExpira_em());

		return MapperUtil.paraTokenRefreshResponse(tokenRefreshRepository.save(token));
	}

	@Transactional(readOnly = true)
	public List<TokenRefreshResponse> listar(UUID idUsuario, String token) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		UUID filtroUsuario = idUsuario;

		if (!autenticado.isAdmin()) {
			filtroUsuario = autenticado.getId();
		}

		return tokenRefreshRepository.filtrar(filtroUsuario, token).stream()
				.map(MapperUtil::paraTokenRefreshResponse)
				.toList();
	}

	@Transactional
	public TokenRefreshResponse alterar(TokenRefreshUpdateRequest request) {
		segurancaUtil.exigirAdminOuProprioUsuario(request.getId_usuario());

		TokenRefresh token = tokenRefreshRepository.findById(request.getId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Token refresh não encontrado"));

		segurancaUtil.exigirAdminOuProprioUsuario(token.getUsuario().getId());

		Usuario usuario = usuarioRepository.findById(request.getId_usuario())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

		token.setUsuario(usuario);
		token.setToken(request.getToken());
		token.setExpiraEm(request.getExpira_em());

		return MapperUtil.paraTokenRefreshResponse(tokenRefreshRepository.save(token));
	}

	@Transactional
	public void apagar(UUID id) {
		TokenRefresh token = tokenRefreshRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Token refresh não encontrado"));
		segurancaUtil.exigirAdminOuProprioUsuario(token.getUsuario().getId());
		tokenRefreshRepository.delete(token);
	}
}

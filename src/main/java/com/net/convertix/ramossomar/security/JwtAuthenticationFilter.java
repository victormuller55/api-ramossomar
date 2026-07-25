package com.net.convertix.ramossomar.security;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtUtil jwtUtil;
	private final UsuarioRepository usuarioRepository;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, @Lazy UsuarioRepository usuarioRepository) {
		this.jwtUtil = jwtUtil;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorization != null && authorization.startsWith("Bearer ")) {
			String token = authorization.substring(7);
			try {
				if (!jwtUtil.tokenValido(token)) {
					SecurityContextHolder.clearContext();
				} else {
					UUID idUsuario = jwtUtil.extrairIdUsuario(token);
					Perfil tipoToken = jwtUtil.extrairPerfil(token);
					var usuarioOpt = usuarioRepository.findById(idUsuario);

					if (usuarioOpt.isPresent()
							&& Boolean.TRUE.equals(usuarioOpt.get().getAtivo())
							&& claimsCompativeisComBanco(usuarioOpt.get(), tipoToken)) {
						UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuarioOpt.get());
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								usuarioAutenticado,
								null,
								usuarioAutenticado.getAuthorities()
						);
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					} else {
						SecurityContextHolder.clearContext();
					}
				}
			} catch (Exception ex) {
				SecurityContextHolder.clearContext();
				log.debug("Falha na autenticação JWT");
			}
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Evita claims stale após mudança de perfil no banco.
	 */
	private boolean claimsCompativeisComBanco(Usuario usuario, Perfil tipoToken) {
		return usuario.getPerfil() == tipoToken;
	}
}

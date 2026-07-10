package com.net.convertix.ramossomar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UsuarioRepository usuarioRepository;
	private final ObjectMapper objectMapper;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
		this.jwtUtil = jwtUtil;
		this.usuarioRepository = usuarioRepository;
		this.objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.substring(7);

		try {
			if (!jwtUtil.tokenValido(token)) {
				escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALIDO", "Token JWT inválido ou expirado");
				return;
			}

			UUID idUsuario = jwtUtil.extrairIdUsuario(token);
			var usuarioOpt = usuarioRepository.findById(idUsuario);

			if (usuarioOpt.isEmpty() || !Boolean.TRUE.equals(usuarioOpt.get().getAtivo())) {
				escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "USUARIO_INVALIDO", "Usuário do token não encontrado ou inativo");
				return;
			}

			UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuarioOpt.get());
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					usuarioAutenticado,
					null,
					usuarioAutenticado.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);
		} catch (Exception ex) {
			escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALIDO", "Token JWT inválido ou expirado");
		}
	}

	private void escreverErro(HttpServletResponse response, int status, String erro, String mensagem) throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), new ErroResponse(status, erro, mensagem));
	}
}

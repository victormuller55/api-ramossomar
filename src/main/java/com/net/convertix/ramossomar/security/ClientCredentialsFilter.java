package com.net.convertix.ramossomar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Identifica o cliente aplicativo (app mobile) via X-Client-Id / X-Client-Secret.
 * Não prova autenticidade criptográfica do binário; é barreira + telemetria.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 25)
@Profile("!test")
public class ClientCredentialsFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(ClientCredentialsFilter.class);

	private final boolean enabled;
	private final String clientId;
	private final String clientSecret;
	private final ObjectMapper objectMapper;

	public ClientCredentialsFilter(
			@Value("${app.client.enabled:false}") boolean enabled,
			@Value("${app.client.id:}") String clientId,
			@Value("${app.client.secret:}") String clientSecret
	) {
		this.enabled = enabled;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

		if (enabled && (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret))) {
			throw new IllegalStateException(
					"app.client.enabled=true exige APP_CLIENT_ID e APP_CLIENT_SECRET configurados"
			);
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		if (!enabled) {
			return true;
		}
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		String path = request.getRequestURI();
		if (path == null) {
			return false;
		}
		// Assets públicos e Swagger (quando ligado) não exigem client credentials
		return path.startsWith("/uploads/")
				|| path.startsWith("/swagger-ui")
				|| path.startsWith("/v3/api-docs")
				|| path.startsWith("/webjars/");
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String requestClientId = request.getHeader("X-Client-Id");
		String requestClientSecret = request.getHeader("X-Client-Secret");

		boolean idOk = constantTimeEquals(clientId, requestClientId);
		boolean secretOk = constantTimeEquals(clientSecret, requestClientSecret);

		if (!idOk || !secretOk) {
			log.debug("Client credentials inválidas para path={}", request.getRequestURI());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");
			objectMapper.writeValue(
					response.getWriter(),
					new ErroResponse(401, "CLIENTE_INVALIDO", "Identificação do aplicativo inválida")
			);
			return;
		}

		request.setAttribute("app.client.id", clientId);
		String appVersion = request.getHeader("X-App-Version");
		String platform = request.getHeader("X-Platform");
		String deviceId = request.getHeader("X-Device-Id");
		if (StringUtils.hasText(appVersion) || StringUtils.hasText(platform) || StringUtils.hasText(deviceId)) {
			log.debug(
					"App client_id={} version={} platform={} device_id={}",
					clientId,
					appVersion,
					platform,
					deviceId
			);
		}

		filterChain.doFilter(request, response);
	}

	private static boolean constantTimeEquals(String expected, String actual) {
		if (expected == null || actual == null) {
			return false;
		}
		byte[] a = expected.getBytes(StandardCharsets.UTF_8);
		byte[] b = actual.getBytes(StandardCharsets.UTF_8);
		return MessageDigest.isEqual(a, b);
	}
}

package com.net.convertix.ramossomar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@Profile("!test")
public class RateLimitFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;
	private final long loginPerMinute;
	private final long cadastroPerMinute;
	private final long apiPerMinute;
	private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	public RateLimitFilter(
			@Value("${app.rate-limit.login-per-minute:5}") long loginPerMinute,
			@Value("${app.rate-limit.cadastro-per-minute:10}") long cadastroPerMinute,
			@Value("${app.rate-limit.api-per-minute:100}") long apiPerMinute
	) {
		this.objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		this.loginPerMinute = loginPerMinute;
		this.cadastroPerMinute = cadastroPerMinute;
		this.apiPerMinute = apiPerMinute;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
			return;
		}

		String path = request.getRequestURI();
		String ip = resolverIp(request);
		LimitRule rule = resolverRegra(path, request.getMethod());
		String bucketKey = rule.name() + ":" + ip;

		Bucket bucket = buckets.computeIfAbsent(bucketKey, key -> criarBucket(rule.limit()));
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		response.setHeader("X-RateLimit-Limit", String.valueOf(rule.limit()));
		response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(probe.getRemainingTokens(), 0)));

		if (!probe.isConsumed()) {
			long waitSeconds = Math.max(1, probe.getNanosToWaitForRefill() / 1_000_000_000L);
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			response.setHeader("Retry-After", String.valueOf(waitSeconds));
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");
			objectMapper.writeValue(
					response.getWriter(),
					new ErroResponse(
							429,
							"RATE_LIMIT",
							"Limite de requisições excedido. Tente novamente em breve."
					)
			);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private Bucket criarBucket(long limit) {
		Bandwidth bandwidth = Bandwidth.builder()
				.capacity(limit)
				.refillGreedy(limit, Duration.ofMinutes(1))
				.build();
		return Bucket.builder().addLimit(bandwidth).build();
	}

	private LimitRule resolverRegra(String path, String method) {
		if ("POST".equalsIgnoreCase(method) && path != null && path.endsWith("/auth/login")) {
			return new LimitRule("LOGIN", loginPerMinute);
		}
		if ("POST".equalsIgnoreCase(method) && path != null && path.endsWith("/usuarios/novo")) {
			return new LimitRule("CADASTRO", cadastroPerMinute);
		}
		return new LimitRule("API", apiPerMinute);
	}

	/**
	 * Confia em X-Forwarded-For somente se o reverse proxy sobrescrever/strip o header.
	 */
	private String resolverIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		String realIp = request.getHeader("X-Real-IP");
		if (realIp != null && !realIp.isBlank()) {
			return realIp.trim();
		}
		return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
	}

	private record LimitRule(String name, long limit) {
	}
}

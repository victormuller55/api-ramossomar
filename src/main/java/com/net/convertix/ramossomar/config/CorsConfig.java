package com.net.convertix.ramossomar.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Value("${app.cors.allowed-origins:}")
	private String allowedOrigins;

	@Value("${app.cors.allowed-methods}")
	private String allowedMethods;

	@Value("${app.cors.allowed-headers}")
	private String allowedHeaders;

	@Value("${app.cors.exposed-headers}")
	private String exposedHeaders;

	@Value("${app.cors.max-age:3600}")
	private long maxAge;

	@Value("${app.cors.allow-credentials:false}")
	private boolean allowCredentials;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// Assets públicos: qualquer origem (GET/OPTIONS), sem credentials
		CorsConfiguration publicCors = new CorsConfiguration();
		publicCors.setAllowedOriginPatterns(List.of("*"));
		publicCors.setAllowedMethods(List.of("GET", "OPTIONS"));
		publicCors.setAllowedHeaders(List.of(
				"Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With",
				"X-Client-Id", "X-Client-Secret", "X-App-Version", "X-Platform", "X-Device-Id"
		));
		publicCors.setExposedHeaders(split(exposedHeaders));
		publicCors.setAllowCredentials(false);
		publicCors.setMaxAge(maxAge);
		source.registerCorsConfiguration("/uploads/**", publicCors);

		// Demais rotas: origens explícitas (painel web). App nativo ignora CORS.
		CorsConfiguration privateCors = new CorsConfiguration();
		List<String> origins = split(allowedOrigins);
		if (origins.isEmpty()) {
			// Sem painel web configurado: não abre "*", evita CORS permissivo em prod
			privateCors.setAllowedOrigins(List.of());
		} else {
			privateCors.setAllowedOrigins(origins);
		}
		privateCors.setAllowedMethods(split(allowedMethods));
		privateCors.setAllowedHeaders(split(allowedHeaders));
		privateCors.setExposedHeaders(split(exposedHeaders));
		privateCors.setAllowCredentials(allowCredentials);
		privateCors.setMaxAge(maxAge);
		source.registerCorsConfiguration("/**", privateCors);

		return source;
	}

	private static List<String> split(String value) {
		if (!StringUtils.hasText(value)) {
			return List.of();
		}
		return Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(StringUtils::hasText)
				.toList();
	}
}

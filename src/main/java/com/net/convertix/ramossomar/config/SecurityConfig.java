package com.net.convertix.ramossomar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.security.ClientCredentialsFilter;
import com.net.convertix.ramossomar.security.JwtAuthenticationFilter;
import com.net.convertix.ramossomar.security.RateLimitFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final RateLimitFilter rateLimitFilter;
	private final ClientCredentialsFilter clientCredentialsFilter;
	private final CorsConfigurationSource corsConfigurationSource;
	private final ObjectMapper objectMapperErro;
	private final boolean swaggerEnabled;

	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			RateLimitFilter rateLimitFilter,
			ClientCredentialsFilter clientCredentialsFilter,
			CorsConfigurationSource corsConfigurationSource,
			@Value("${springdoc.swagger-ui.enabled:false}") boolean swaggerEnabled
	) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.rateLimitFilter = rateLimitFilter;
		this.clientCredentialsFilter = clientCredentialsFilter;
		this.corsConfigurationSource = corsConfigurationSource;
		this.swaggerEnabled = swaggerEnabled;
		this.objectMapperErro = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.headers(headers -> {
					headers
							.contentTypeOptions(Customizer.withDefaults())
							.frameOptions(frame -> frame.deny())
							.httpStrictTransportSecurity(hsts -> hsts
									.includeSubDomains(true)
									.preload(true)
									.maxAgeInSeconds(31536000))
							.referrerPolicy(referrer -> referrer.policy(
									ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
							.permissionsPolicyHeader(permissions -> permissions.policy(
									"geolocation=(), microphone=(), camera=(), payment=(), usb=()"))
							.cacheControl(Customizer.withDefaults());

					if (swaggerEnabled) {
						headers.contentSecurityPolicy(csp -> csp.policyDirectives(
								"default-src 'self'; "
										+ "script-src 'self' 'unsafe-inline'; "
										+ "style-src 'self' 'unsafe-inline'; "
										+ "img-src 'self' data:; "
										+ "font-src 'self' data:; "
										+ "connect-src 'self'; "
										+ "frame-ancestors 'none'; "
										+ "base-uri 'self'; "
										+ "form-action 'self'"));
					} else {
						headers.contentSecurityPolicy(csp -> csp.policyDirectives(
								"default-src 'none'; frame-ancestors 'none'; base-uri 'none'; form-action 'none'"));
					}
				})
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers("/api/v1/ramossomar/auth/**").permitAll()
							.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
							.requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
							.requestMatchers(HttpMethod.POST, "/api/v1/ramossomar/webhook/**").permitAll();

					if (swaggerEnabled) {
						auth.requestMatchers(
								"/swagger-ui.html",
								"/swagger-ui/**",
								"/v3/api-docs",
								"/v3/api-docs/**",
								"/swagger-resources/**",
								"/webjars/**"
						).permitAll();
					} else {
						auth.requestMatchers(
								"/swagger-ui.html",
								"/swagger-ui/**",
								"/v3/api-docs",
								"/v3/api-docs/**",
								"/swagger-ui/index.html"
						).denyAll();
					}

					auth.requestMatchers(HttpMethod.POST, "/api/v1/ramossomar/usuarios/novo").hasRole("ADMIN")
							.requestMatchers(HttpMethod.GET, "/api/v1/ramossomar/usuarios").hasRole("ADMIN")
							.requestMatchers(HttpMethod.DELETE, "/api/v1/ramossomar/usuarios/**").hasRole("ADMIN")
							.requestMatchers("/api/v1/ramossomar/relatorios/**").hasRole("ADMIN")
							.anyRequest().authenticated();
				})
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType(MediaType.APPLICATION_JSON_VALUE);
							response.setCharacterEncoding("UTF-8");
							objectMapperErro.writeValue(
									response.getWriter(),
									new ErroResponse(401, "NAO_AUTENTICADO", "Token JWT ausente ou inválido")
							);
						})
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.setContentType(MediaType.APPLICATION_JSON_VALUE);
							response.setCharacterEncoding("UTF-8");
							objectMapperErro.writeValue(
									response.getWriter(),
									new ErroResponse(403, "ACESSO_NEGADO", "Você não tem permissão para acessar este recurso")
							);
						})
				)
				.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(clientCredentialsFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
}

package com.net.convertix.ramossomar.config;

import com.net.convertix.ramossomar.security.ClientCredentialsFilter;
import com.net.convertix.ramossomar.security.JwtAuthenticationFilter;
import com.net.convertix.ramossomar.security.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Impede registro duplicado no container Servlet dos filtros que já entram
 * via SecurityFilterChain.
 */
@Configuration
@Profile("!test")
public class FilterRegistrationConfig {

	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> disableJwtFilterServletRegistration(
			JwtAuthenticationFilter filter
	) {
		FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<RateLimitFilter> disableRateLimitFilterServletRegistration(
			RateLimitFilter filter
	) {
		FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<ClientCredentialsFilter> disableClientCredentialsFilterServletRegistration(
			ClientCredentialsFilter filter
	) {
		FilterRegistrationBean<ClientCredentialsFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}
}

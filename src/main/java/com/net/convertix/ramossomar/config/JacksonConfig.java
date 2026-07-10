package com.net.convertix.ramossomar.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;

@Configuration
public class JacksonConfig {

	@Bean
	JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
		return builder -> builder
				.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
}

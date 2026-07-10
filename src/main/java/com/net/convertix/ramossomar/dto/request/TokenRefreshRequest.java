package com.net.convertix.ramossomar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados para criação de token refresh")
public class TokenRefreshRequest {

	@NotNull(message = "O id do usuário é obrigatório")
	@Schema(description = "Identificador do usuário")
	private UUID id_usuario;

	@NotBlank(message = "O token é obrigatório")
	@Size(max = 500)
	@Schema(description = "Valor do token refresh")
	private String token;

	@NotNull(message = "A data de expiração é obrigatória")
	@Schema(description = "Data/hora de expiração")
	private LocalDateTime expira_em;

	public UUID getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(UUID id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpira_em() {
		return expira_em;
	}

	public void setExpira_em(LocalDateTime expira_em) {
		this.expira_em = expira_em;
	}
}

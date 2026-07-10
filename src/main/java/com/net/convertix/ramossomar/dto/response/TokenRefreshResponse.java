package com.net.convertix.ramossomar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de resposta do token refresh")
public class TokenRefreshResponse {

	private UUID id;
	private UUID id_usuario;
	private String token;
	private LocalDateTime expira_em;
	private LocalDateTime data_criacao;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public LocalDateTime getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(LocalDateTime data_criacao) {
		this.data_criacao = data_criacao;
	}
}

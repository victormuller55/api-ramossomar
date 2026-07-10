package com.net.convertix.ramossomar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Dados para criação manual de histórico (uso administrativo)")
public class HistoricoApoiadorRequest {

	@NotNull(message = "O id do apoiador é obrigatório")
	private UUID id_apoiador;

	@NotNull(message = "O id do usuário é obrigatório")
	private UUID id_usuario;

	@NotBlank(message = "O campo alterado é obrigatório")
	@Size(max = 100)
	private String campo_alterado;

	private String valor_anterior;

	private String valor_novo;

	public UUID getId_apoiador() {
		return id_apoiador;
	}

	public void setId_apoiador(UUID id_apoiador) {
		this.id_apoiador = id_apoiador;
	}

	public UUID getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(UUID id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getCampo_alterado() {
		return campo_alterado;
	}

	public void setCampo_alterado(String campo_alterado) {
		this.campo_alterado = campo_alterado;
	}

	public String getValor_anterior() {
		return valor_anterior;
	}

	public void setValor_anterior(String valor_anterior) {
		this.valor_anterior = valor_anterior;
	}

	public String getValor_novo() {
		return valor_novo;
	}

	public void setValor_novo(String valor_novo) {
		this.valor_novo = valor_novo;
	}
}

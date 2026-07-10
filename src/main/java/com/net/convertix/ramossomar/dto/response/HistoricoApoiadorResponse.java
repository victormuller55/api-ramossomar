package com.net.convertix.ramossomar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de resposta do histórico de apoiador")
public class HistoricoApoiadorResponse {

	private UUID id;
	private UUID id_apoiador;
	private UUID id_usuario;
	private String nome_usuario;
	private String campo_alterado;
	private String valor_anterior;
	private String valor_novo;
	private LocalDateTime data_alteracao;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public String getNome_usuario() {
		return nome_usuario;
	}

	public void setNome_usuario(String nome_usuario) {
		this.nome_usuario = nome_usuario;
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

	public LocalDateTime getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(LocalDateTime data_alteracao) {
		this.data_alteracao = data_alteracao;
	}
}

package com.net.convertix.ramossomar.dto.response;

import com.net.convertix.ramossomar.model.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resposta de autenticação com JWT")
public class LoginResponse {

	@Schema(description = "Token JWT de acesso")
	private String access_token;

	@Schema(description = "Token de refresh")
	private String refresh_token;

	@Schema(description = "Tipo do token", example = "Bearer")
	private String tipo_token = "Bearer";

	@Schema(description = "Data/hora de expiração do access token")
	private LocalDateTime expira_em;

	@Schema(description = "Identificador do usuário")
	private UUID id_usuario;

	@Schema(description = "Nome do usuário")
	private String nome;

	@Schema(description = "E-mail do usuário")
	private String email;

	@Schema(description = "Perfil do usuário")
	private Perfil perfil;

	@Schema(description = "Telefone do usuário")
	private String telefone;

	@Schema(description = "Caminho ou URL da imagem de perfil")
	private String imagem;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getTipo_token() {
		return tipo_token;
	}

	public void setTipo_token(String tipo_token) {
		this.tipo_token = tipo_token;
	}

	public LocalDateTime getExpira_em() {
		return expira_em;
	}

	public void setExpira_em(LocalDateTime expira_em) {
		this.expira_em = expira_em;
	}

	public UUID getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(UUID id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
}

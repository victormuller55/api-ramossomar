package com.net.convertix.ramossomar.dto.response;

import com.net.convertix.ramossomar.model.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de resposta do usuário")
public class UsuarioResponse {

	@Schema(description = "Identificador do usuário")
	private UUID id;

	@Schema(description = "Nome completo")
	private String nome;

	@Schema(description = "E-mail")
	private String email;

	@Schema(description = "Perfil de acesso")
	private Perfil perfil;

	@Schema(description = "Telefone")
	private String telefone;

	@Schema(description = "Caminho ou URL da imagem")
	private String imagem;

	@Schema(description = "Indica se está ativo")
	private Boolean ativo;

	@Schema(description = "Data do último login")
	private LocalDateTime data_ultimo_login;

	@Schema(description = "Data de criação")
	private LocalDateTime data_criacao;

	@Schema(description = "Data da última atualização")
	private LocalDateTime data_atualizacao;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public LocalDateTime getData_ultimo_login() {
		return data_ultimo_login;
	}

	public void setData_ultimo_login(LocalDateTime data_ultimo_login) {
		this.data_ultimo_login = data_ultimo_login;
	}

	public LocalDateTime getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(LocalDateTime data_criacao) {
		this.data_criacao = data_criacao;
	}

	public LocalDateTime getData_atualizacao() {
		return data_atualizacao;
	}

	public void setData_atualizacao(LocalDateTime data_atualizacao) {
		this.data_atualizacao = data_atualizacao;
	}
}

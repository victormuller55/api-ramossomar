package com.net.convertix.ramossomar.dto.request;

import com.net.convertix.ramossomar.model.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de usuário")
public class UsuarioRequest {

	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
	@Schema(description = "Nome completo do usuário", example = "João Silva")
	private String nome;

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "E-mail inválido")
	@Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
	@Schema(description = "E-mail único do usuário", example = "joao@email.com")
	private String email;

	@NotBlank(message = "A senha é obrigatória")
	@Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
	@Schema(description = "Senha em texto (será criptografada)", example = "senha123")
	private String senha;

	@NotNull(message = "O perfil é obrigatório")
	@Schema(description = "Perfil de acesso", example = "LIDER")
	private Perfil perfil;

	@Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
	@Schema(description = "Telefone de contato", example = "41999999999")
	private String telefone;

	@Size(max = 500, message = "A imagem deve ter no máximo 500 caracteres")
	@Schema(description = "Caminho ou URL da imagem", example = "/uploads/usuarios/joao.jpg")
	private String imagem;

	@Schema(description = "Indica se o usuário está ativo", example = "true")
	private Boolean ativo = true;

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

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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
}

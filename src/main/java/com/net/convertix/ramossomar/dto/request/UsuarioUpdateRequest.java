package com.net.convertix.ramossomar.dto.request;

import com.net.convertix.ramossomar.model.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Dados para alteração de usuário")
public class UsuarioUpdateRequest {

	@NotNull(message = "O id é obrigatório")
	@Schema(description = "Identificador do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID id;

	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
	@Schema(description = "Nome completo do usuário", example = "João Silva")
	private String nome;

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "E-mail inválido")
	@Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
	@Schema(description = "E-mail único do usuário", example = "joao@email.com")
	private String email;

	@Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
	@Schema(description = "Nova senha (opcional). Se informada, será criptografada", example = "novaSenha123")
	private String senha;

	@NotNull(message = "O perfil é obrigatório")
	@Schema(description = "Perfil de acesso", example = "LIDER")
	private Perfil perfil;

	@Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
	@Schema(description = "Telefone de contato", example = "41999999999")
	private String telefone;

	@NotNull(message = "O campo ativo é obrigatório")
	@Schema(description = "Indica se o usuário está ativo", example = "true")
	private Boolean ativo;

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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}

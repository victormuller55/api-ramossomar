package com.net.convertix.ramossomar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credenciais de autenticação")
public class LoginRequest {

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "E-mail inválido")
	@Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
	@Schema(description = "E-mail do usuário", example = "admin@ramossomar.com")
	private String email;

	@NotBlank(message = "A senha é obrigatória")
	@Size(min = 8, max = 128, message = "A senha deve ter entre 8 e 128 caracteres")
	@Schema(description = "Senha do usuário", example = "senha1234")
	private String senha;

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
}

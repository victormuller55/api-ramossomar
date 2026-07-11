package com.net.convertix.ramossomar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Dados para alteração de publicação (imagens via upload separado)")
public class PublicacaoUpdateRequest {

	@NotNull(message = "O id é obrigatório")
	@Schema(description = "Identificador da publicação")
	private UUID id;

	@NotNull(message = "O id do autor é obrigatório")
	@Schema(description = "Identificador do autor")
	private UUID id_autor;

	@NotBlank(message = "O título é obrigatório")
	@Size(max = 200)
	@Schema(description = "Título da publicação")
	private String titulo;

	@NotBlank(message = "O conteúdo é obrigatório")
	@Schema(description = "Conteúdo da publicação")
	private String conteudo;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getId_autor() {
		return id_autor;
	}

	public void setId_autor(UUID id_autor) {
		this.id_autor = id_autor;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
}

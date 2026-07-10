package com.net.convertix.ramossomar.dto.request;

import com.net.convertix.ramossomar.model.enums.TipoMidia;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Dados para criação de publicação")
public class PublicacaoRequest {

	@NotNull(message = "O id do autor é obrigatório")
	@Schema(description = "Identificador do autor", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID id_autor;

	@NotBlank(message = "O título é obrigatório")
	@Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
	@Schema(description = "Título da publicação", example = "Campanha de rua")
	private String titulo;

	@NotBlank(message = "O conteúdo é obrigatório")
	@Schema(description = "Conteúdo da publicação")
	private String conteudo;

	@Size(max = 500, message = "A mídia deve ter no máximo 500 caracteres")
	@Schema(description = "Caminho ou URL da mídia", example = "/uploads/publicacoes/foto.jpg")
	private String midia;

	@Schema(description = "Tipo da mídia", example = "IMAGEM")
	private TipoMidia tipo_midia;

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

	public String getMidia() {
		return midia;
	}

	public void setMidia(String midia) {
		this.midia = midia;
	}

	public TipoMidia getTipo_midia() {
		return tipo_midia;
	}

	public void setTipo_midia(TipoMidia tipo_midia) {
		this.tipo_midia = tipo_midia;
	}
}

package com.net.convertix.ramossomar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados de resposta da publicação")
public class PublicacaoResponse {

	private UUID id;
	private UUID id_autor;
	private String nome_autor;
	private String imagem_autor;
	private String titulo;
	private String conteudo;
	private List<String> imagens = new ArrayList<>();
	private LocalDateTime data_criacao;
	private LocalDateTime data_atualizacao;

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

	public String getNome_autor() {
		return nome_autor;
	}

	public void setNome_autor(String nome_autor) {
		this.nome_autor = nome_autor;
	}

	public String getImagem_autor() {
		return imagem_autor;
	}

	public void setImagem_autor(String imagem_autor) {
		this.imagem_autor = imagem_autor;
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

	public List<String> getImagens() {
		return imagens;
	}

	public void setImagens(List<String> imagens) {
		this.imagens = imagens;
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

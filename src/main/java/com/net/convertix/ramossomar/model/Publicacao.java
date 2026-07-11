package com.net.convertix.ramossomar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
		name = "tbl_publicacoes",
		indexes = {
				@Index(name = "idx_publicacoes_data_criacao", columnList = "data_criacao")
		}
)
public class Publicacao {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_autor", nullable = false, columnDefinition = "CHAR(36)")
	private Usuario autor;

	@Column(name = "titulo", nullable = false, length = 200)
	private String titulo;

	@Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
	private String conteudo;

	@Column(name = "imagem_1", length = 500)
	private String imagem1;

	@Column(name = "imagem_2", length = 500)
	private String imagem2;

	@Column(name = "imagem_3", length = 500)
	private String imagem3;

	@Column(name = "data_criacao", nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private LocalDateTime dataAtualizacao;

	@PrePersist
	public void prePersist() {
		LocalDateTime agora = LocalDateTime.now();
		this.dataCriacao = agora;
		this.dataAtualizacao = agora;
	}

	@PreUpdate
	public void preUpdate() {
		this.dataAtualizacao = LocalDateTime.now();
	}

	public List<String> obterImagens() {
		List<String> imagens = new ArrayList<>();
		if (imagem1 != null && !imagem1.isBlank()) {
			imagens.add(imagem1);
		}
		if (imagem2 != null && !imagem2.isBlank()) {
			imagens.add(imagem2);
		}
		if (imagem3 != null && !imagem3.isBlank()) {
			imagens.add(imagem3);
		}
		return imagens;
	}

	public void definirImagens(List<String> imagens) {
		this.imagem1 = null;
		this.imagem2 = null;
		this.imagem3 = null;
		if (imagens == null || imagens.isEmpty()) {
			return;
		}
		if (imagens.size() > 0) {
			this.imagem1 = imagens.get(0);
		}
		if (imagens.size() > 1) {
			this.imagem2 = imagens.get(1);
		}
		if (imagens.size() > 2) {
			this.imagem3 = imagens.get(2);
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
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

	public String getImagem1() {
		return imagem1;
	}

	public void setImagem1(String imagem1) {
		this.imagem1 = imagem1;
	}

	public String getImagem2() {
		return imagem2;
	}

	public void setImagem2(String imagem2) {
		this.imagem2 = imagem2;
	}

	public String getImagem3() {
		return imagem3;
	}

	public void setImagem3(String imagem3) {
		this.imagem3 = imagem3;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
}

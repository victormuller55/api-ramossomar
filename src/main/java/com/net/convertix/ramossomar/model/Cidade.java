package com.net.convertix.ramossomar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
		name = "tbl_cidades",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_cidades_codigo_ibge", columnNames = "codigo_ibge")
		},
		indexes = {
				@Index(name = "idx_cidades_codigo_ibge", columnList = "codigo_ibge"),
				@Index(name = "idx_cidades_nome", columnList = "nome"),
				@Index(name = "idx_cidades_uf", columnList = "uf")
		}
)
public class Cidade {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	private UUID id;

	@Column(name = "codigo_ibge", nullable = false, unique = true, length = 10)
	private String codigoIbge;

	@Column(name = "nome", nullable = false, length = 150)
	private String nome;

	@Column(name = "uf", nullable = false, length = 2)
	private String uf;

	@Column(name = "data_criacao", nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private LocalDateTime dataAtualizacao;

	@OneToMany(mappedBy = "cidade")
	private List<LocalVotacao> locaisVotacao = new ArrayList<>();

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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCodigoIbge() {
		return codigoIbge;
	}

	public void setCodigoIbge(String codigoIbge) {
		this.codigoIbge = codigoIbge;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
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

	public List<LocalVotacao> getLocaisVotacao() {
		return locaisVotacao;
	}

	public void setLocaisVotacao(List<LocalVotacao> locaisVotacao) {
		this.locaisVotacao = locaisVotacao;
	}
}

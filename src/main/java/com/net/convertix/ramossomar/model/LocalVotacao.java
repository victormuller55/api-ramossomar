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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
		name = "tbl_locais_votacao",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_locais_votacao_codigo_tse", columnNames = "codigo_tse")
		},
		indexes = {
				@Index(name = "idx_locais_votacao_codigo_tse", columnList = "codigo_tse"),
				@Index(name = "idx_locais_votacao_nome", columnList = "nome"),
				@Index(name = "idx_locais_votacao_zona", columnList = "zona_eleitoral"),
				@Index(name = "idx_locais_votacao_ativo", columnList = "ativo"),
				@Index(name = "idx_locais_votacao_id_cidade", columnList = "id_cidade")
		}
)
public class LocalVotacao {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	private UUID id;

	@Column(name = "codigo_tse", nullable = false, unique = true, length = 50)
	private String codigoTse;

	@Column(name = "nome", nullable = false, length = 200)
	private String nome;

	@Column(name = "endereco", nullable = false, length = 300)
	private String endereco;

	@Column(name = "bairro", length = 100)
	private String bairro;

	@Column(name = "cep", length = 10)
	private String cep;

	@Column(name = "zona_eleitoral", nullable = false, length = 10)
	private String zonaEleitoral;

	@Column(name = "latitude", precision = 12, scale = 8)
	private BigDecimal latitude;

	@Column(name = "longitude", precision = 12, scale = 8)
	private BigDecimal longitude;

	@Column(name = "ativo", nullable = false)
	private Boolean ativo = true;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_cidade", nullable = false, columnDefinition = "CHAR(36)")
	private Cidade cidade;

	@Column(name = "data_criacao", nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private LocalDateTime dataAtualizacao;

	@PrePersist
	public void prePersist() {
		LocalDateTime agora = LocalDateTime.now();
		this.dataCriacao = agora;
		this.dataAtualizacao = agora;
		if (this.ativo == null) {
			this.ativo = true;
		}
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

	public String getCodigoTse() {
		return codigoTse;
	}

	public void setCodigoTse(String codigoTse) {
		this.codigoTse = codigoTse;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getZonaEleitoral() {
		return zonaEleitoral;
	}

	public void setZonaEleitoral(String zonaEleitoral) {
		this.zonaEleitoral = zonaEleitoral;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
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

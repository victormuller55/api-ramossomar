package com.net.convertix.ramossomar.model;

import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
		name = "tbl_apoiadores",
		indexes = {
				@Index(name = "idx_apoiadores_cpf", columnList = "cpf"),
				@Index(name = "idx_apoiadores_id_lider", columnList = "id_lider"),
				@Index(name = "idx_apoiadores_cidade", columnList = "cidade"),
				@Index(name = "idx_apoiadores_intencao_voto", columnList = "intencao_voto"),
				@Index(name = "idx_apoiadores_data_criacao", columnList = "data_criacao")
		}
)
public class Apoiador {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_lider", nullable = false, columnDefinition = "CHAR(36)")
	private Usuario lider;

	@Column(name = "nome", nullable = false, length = 150)
	private String nome;

	@Column(name = "cpf", nullable = false, length = 14)
	private String cpf;

	@Column(name = "data_nascimento")
	private LocalDate dataNascimento;

	@Column(name = "telefone", length = 20)
	private String telefone;

	@Column(name = "whatsapp", length = 20)
	private String whatsapp;

	@Column(name = "cep", length = 10)
	private String cep;

	@Column(name = "endereco", length = 200)
	private String endereco;

	@Column(name = "numero", length = 20)
	private String numero;

	@Column(name = "complemento", length = 100)
	private String complemento;

	@Column(name = "bairro", length = 100)
	private String bairro;

	@Column(name = "cidade", length = 100)
	private String cidade;

	@Column(name = "local_votacao", length = 200)
	private String localVotacao;

	@Enumerated(EnumType.STRING)
	@Column(name = "intencao_voto", nullable = false, length = 20)
	private IntencaoVoto intencaoVoto;

	@Column(name = "observacoes", columnDefinition = "TEXT")
	private String observacoes;

	@Column(name = "data_criacao", nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private LocalDateTime dataAtualizacao;

	@Column(name = "data_exclusao")
	private LocalDateTime dataExclusao;

	@OneToMany(mappedBy = "apoiador")
	private List<HistoricoApoiador> historicos = new ArrayList<>();

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

	public boolean isExcluido() {
		return this.dataExclusao != null;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Usuario getLider() {
		return lider;
	}

	public void setLider(Usuario lider) {
		this.lider = lider;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getLocalVotacao() {
		return localVotacao;
	}

	public void setLocalVotacao(String localVotacao) {
		this.localVotacao = localVotacao;
	}

	public IntencaoVoto getIntencaoVoto() {
		return intencaoVoto;
	}

	public void setIntencaoVoto(IntencaoVoto intencaoVoto) {
		this.intencaoVoto = intencaoVoto;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
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

	public LocalDateTime getDataExclusao() {
		return dataExclusao;
	}

	public void setDataExclusao(LocalDateTime dataExclusao) {
		this.dataExclusao = dataExclusao;
	}

	public List<HistoricoApoiador> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<HistoricoApoiador> historicos) {
		this.historicos = historicos;
	}
}

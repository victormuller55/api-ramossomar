package com.net.convertix.ramossomar.model;

import com.net.convertix.ramossomar.model.enums.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
		name = "tbl_usuarios",
		indexes = {
				@Index(name = "idx_usuarios_email", columnList = "email", unique = true),
				@Index(name = "idx_usuarios_data_criacao", columnList = "data_criacao")
		}
)
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	private UUID id;

	@Column(name = "nome", nullable = false, length = 150)
	private String nome;

	@Column(name = "email", nullable = false, unique = true, length = 150)
	private String email;

	@Column(name = "senha", nullable = false, length = 255)
	private String senha;

	@Enumerated(EnumType.STRING)
	@Column(name = "perfil", nullable = false, length = 20)
	private Perfil perfil;

	@Column(name = "telefone", length = 20)
	private String telefone;

	@Column(name = "imagem", length = 500)
	private String imagem;

	@Column(name = "ativo", nullable = false)
	private Boolean ativo = true;

	@Column(name = "data_ultimo_login")
	private LocalDateTime dataUltimoLogin;

	@Column(name = "data_criacao", nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private LocalDateTime dataAtualizacao;

	@OneToMany(mappedBy = "lider")
	private List<Apoiador> apoiadores = new ArrayList<>();

	@OneToMany(mappedBy = "autor")
	private List<Publicacao> publicacoes = new ArrayList<>();

	@OneToMany(mappedBy = "usuario")
	private List<TokenRefresh> tokensRefresh = new ArrayList<>();

	@OneToMany(mappedBy = "usuario")
	private List<HistoricoApoiador> historicos = new ArrayList<>();

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

	public LocalDateTime getDataUltimoLogin() {
		return dataUltimoLogin;
	}

	public void setDataUltimoLogin(LocalDateTime dataUltimoLogin) {
		this.dataUltimoLogin = dataUltimoLogin;
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

	public List<Apoiador> getApoiadores() {
		return apoiadores;
	}

	public void setApoiadores(List<Apoiador> apoiadores) {
		this.apoiadores = apoiadores;
	}

	public List<Publicacao> getPublicacoes() {
		return publicacoes;
	}

	public void setPublicacoes(List<Publicacao> publicacoes) {
		this.publicacoes = publicacoes;
	}

	public List<TokenRefresh> getTokensRefresh() {
		return tokensRefresh;
	}

	public void setTokensRefresh(List<TokenRefresh> tokensRefresh) {
		this.tokensRefresh = tokensRefresh;
	}

	public List<HistoricoApoiador> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<HistoricoApoiador> historicos) {
		this.historicos = historicos;
	}
}

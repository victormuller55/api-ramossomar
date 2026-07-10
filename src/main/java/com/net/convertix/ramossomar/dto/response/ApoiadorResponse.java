package com.net.convertix.ramossomar.dto.response;

import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de resposta do apoiador")
public class ApoiadorResponse {

	private UUID id;
	private UUID id_lider;
	private String nome_lider;
	private String nome;
	private String cpf;
	private LocalDate data_nascimento;
	private String telefone;
	private String whatsapp;
	private String cep;
	private String endereco;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String local_votacao;
	private IntencaoVoto intencao_voto;
	private String observacoes;
	private LocalDateTime data_criacao;
	private LocalDateTime data_atualizacao;
	private LocalDateTime data_exclusao;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getId_lider() {
		return id_lider;
	}

	public void setId_lider(UUID id_lider) {
		this.id_lider = id_lider;
	}

	public String getNome_lider() {
		return nome_lider;
	}

	public void setNome_lider(String nome_lider) {
		this.nome_lider = nome_lider;
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

	public LocalDate getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(LocalDate data_nascimento) {
		this.data_nascimento = data_nascimento;
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

	public String getLocal_votacao() {
		return local_votacao;
	}

	public void setLocal_votacao(String local_votacao) {
		this.local_votacao = local_votacao;
	}

	public IntencaoVoto getIntencao_voto() {
		return intencao_voto;
	}

	public void setIntencao_voto(IntencaoVoto intencao_voto) {
		this.intencao_voto = intencao_voto;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
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

	public LocalDateTime getData_exclusao() {
		return data_exclusao;
	}

	public void setData_exclusao(LocalDateTime data_exclusao) {
		this.data_exclusao = data_exclusao;
	}
}

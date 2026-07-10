package com.net.convertix.ramossomar.dto.request;

import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Dados para alteração de apoiador")
public class ApoiadorUpdateRequest {

	@NotNull(message = "O id é obrigatório")
	@Schema(description = "Identificador do apoiador")
	private UUID id;

	@NotNull(message = "O id do líder é obrigatório")
	@Schema(description = "Identificador do líder responsável")
	private UUID id_lider;

	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
	@Schema(description = "Nome do apoiador")
	private String nome;

	@NotBlank(message = "O CPF é obrigatório")
	@Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF inválido")
	@Schema(description = "CPF do apoiador")
	private String cpf;

	@Schema(description = "Data de nascimento")
	private LocalDate data_nascimento;

	@Size(max = 20)
	@Schema(description = "Telefone")
	private String telefone;

	@Size(max = 20)
	@Schema(description = "WhatsApp")
	private String whatsapp;

	@Size(max = 10)
	@Schema(description = "CEP")
	private String cep;

	@Size(max = 200)
	@Schema(description = "Endereço")
	private String endereco;

	@Size(max = 20)
	@Schema(description = "Número")
	private String numero;

	@Size(max = 100)
	@Schema(description = "Complemento")
	private String complemento;

	@Size(max = 100)
	@Schema(description = "Bairro")
	private String bairro;

	@Size(max = 100)
	@Schema(description = "Cidade")
	private String cidade;

	@Size(max = 200)
	@Schema(description = "Local de votação")
	private String local_votacao;

	@NotNull(message = "A intenção de voto é obrigatória")
	@Schema(description = "Intenção de voto")
	private IntencaoVoto intencao_voto;

	@Schema(description = "Observações")
	private String observacoes;

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
}

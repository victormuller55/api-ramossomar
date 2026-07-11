package com.net.convertix.ramossomar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para criação de local de votação")
public class LocalVotacaoRequest {

	@NotBlank(message = "O código TSE é obrigatório")
	@Size(max = 50, message = "O código TSE deve ter no máximo 50 caracteres")
	@Schema(description = "Código oficial do local no TSE", example = "93734-1-1015")
	private String codigo_tse;

	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 200, message = "O nome deve ter no máximo 200 caracteres")
	@Schema(description = "Nome do local de votação", example = "ESCOLA MUNICIPAL CENTRO")
	private String nome;

	@NotBlank(message = "O endereço é obrigatório")
	@Size(max = 300, message = "O endereço deve ter no máximo 300 caracteres")
	@Schema(description = "Endereço do local", example = "RUA DAS FLORES, 100")
	private String endereco;

	@Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
	@Schema(description = "Bairro", example = "CENTRO")
	private String bairro;

	@Size(max = 10, message = "O CEP deve ter no máximo 10 caracteres")
	@Schema(description = "CEP", example = "74000000")
	private String cep;

	@NotBlank(message = "A zona eleitoral é obrigatória")
	@Size(max = 10, message = "A zona eleitoral deve ter no máximo 10 caracteres")
	@Schema(description = "Número da zona eleitoral", example = "1")
	private String zona_eleitoral;

	@Schema(description = "Latitude", example = "-16.68690000")
	private BigDecimal latitude;

	@Schema(description = "Longitude", example = "-49.26480000")
	private BigDecimal longitude;

	@Schema(description = "Indica se o local está ativo", example = "true")
	private Boolean ativo;

	@NotNull(message = "O id da cidade é obrigatório")
	@Schema(description = "Identificador da cidade", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID id_cidade;

	public String getCodigo_tse() {
		return codigo_tse;
	}

	public void setCodigo_tse(String codigo_tse) {
		this.codigo_tse = codigo_tse;
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

	public String getZona_eleitoral() {
		return zona_eleitoral;
	}

	public void setZona_eleitoral(String zona_eleitoral) {
		this.zona_eleitoral = zona_eleitoral;
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

	public UUID getId_cidade() {
		return id_cidade;
	}

	public void setId_cidade(UUID id_cidade) {
		this.id_cidade = id_cidade;
	}
}

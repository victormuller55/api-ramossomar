package com.net.convertix.ramossomar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta padrão de erro da API")
public class ErroResponse {

	@Schema(description = "Código HTTP do erro", example = "400")
	private int status_code;

	@Schema(description = "Tipo ou identificação do erro", example = "VALIDACAO")
	private String erro;

	@Schema(description = "Mensagem amigável para o frontend", example = "Dados inválidos")
	private String mensagem;

	@Schema(description = "Momento em que o erro ocorreu")
	private LocalDateTime timestamp;

	public ErroResponse() {
	}

	public ErroResponse(int statusCode, String erro, String mensagem) {
		this.status_code = statusCode;
		this.erro = erro;
		this.mensagem = mensagem;
		this.timestamp = LocalDateTime.now();
	}

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}

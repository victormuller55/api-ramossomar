package com.net.convertix.ramossomar.exception;

import com.net.convertix.ramossomar.dto.response.ErroResponse;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErroResponse(404, "NAO_ENCONTRADO", ex.getMessage()));
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<ErroResponse> handleNegocio(NegocioException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse(400, "NEGOCIO", ex.getMessage()));
	}

	@ExceptionHandler(AcessoNegadoException.class)
	public ResponseEntity<ErroResponse> handleAcessoNegado(AcessoNegadoException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErroResponse(403, "ACESSO_NEGADO", ex.getMessage()));
	}

	@ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
	public ResponseEntity<ErroResponse> handleSpringAccessDenied(Exception ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErroResponse(403, "ACESSO_NEGADO", "Você não tem permissão para acessar este recurso"));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErroResponse> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErroResponse(401, "CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining("; "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse(400, "VALIDACAO", mensagem));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse(400, "JSON_INVALIDO", "Corpo da requisição inválido ou mal formatado"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResponse> handleGeral(Exception ex) {
		log.error("Erro interno não tratado", ex);
		String mensagem = ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado no servidor";
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErroResponse(500, "ERRO_INTERNO", mensagem));
	}
}

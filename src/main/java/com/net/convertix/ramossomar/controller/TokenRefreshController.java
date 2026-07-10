package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.TokenRefreshRequest;
import com.net.convertix.ramossomar.dto.request.TokenRefreshUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.dto.response.TokenRefreshResponse;
import com.net.convertix.ramossomar.service.TokenRefreshService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ramossomar/tokens-refresh")
@Tag(name = "Tokens", description = "CRUD de tokens refresh")
@SecurityRequirement(name = "bearer-jwt")
public class TokenRefreshController {

	private final TokenRefreshService tokenRefreshService;

	public TokenRefreshController(TokenRefreshService tokenRefreshService) {
		this.tokenRefreshService = tokenRefreshService;
	}

	@PostMapping("/novo")
	@Operation(summary = "Criar token refresh", description = "Cadastra um novo token refresh para um usuário.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Token criado",
					content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<TokenRefreshResponse> novo(@Valid @RequestBody TokenRefreshRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tokenRefreshService.criar(request));
	}

	@GetMapping
	@Operation(summary = "Listar tokens refresh", description = "Lista tokens refresh com filtros via query params.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<TokenRefreshResponse>> listar(
			@RequestParam(required = false, name = "id_usuario") UUID idUsuario,
			@RequestParam(required = false) String token
	) {
		return ResponseEntity.ok(tokenRefreshService.listar(idUsuario, token));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar token refresh", description = "Atualiza os dados de um token refresh.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Token atualizado",
					content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Token não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<TokenRefreshResponse> alterarDados(@Valid @RequestBody TokenRefreshUpdateRequest request) {
		return ResponseEntity.ok(tokenRefreshService.alterar(request));
	}

	@DeleteMapping("/apagar")
	@Operation(summary = "Apagar token refresh", description = "Remove fisicamente um token refresh.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Token removido"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Token não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<Void> apagar(@RequestParam UUID id) {
		tokenRefreshService.apagar(id);
		return ResponseEntity.noContent().build();
	}
}

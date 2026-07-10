package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.HistoricoApoiadorRequest;
import com.net.convertix.ramossomar.dto.request.HistoricoApoiadorUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.dto.response.HistoricoApoiadorResponse;
import com.net.convertix.ramossomar.service.HistoricoApoiadorService;
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
@RequestMapping("/api/v1/ramossomar/historico-apoiadores")
@Tag(name = "Histórico de Apoiadores", description = "Consulta e gestão do histórico de alterações de apoiadores")
@SecurityRequirement(name = "bearer-jwt")
public class HistoricoApoiadorController {

	private final HistoricoApoiadorService historicoApoiadorService;

	public HistoricoApoiadorController(HistoricoApoiadorService historicoApoiadorService) {
		this.historicoApoiadorService = historicoApoiadorService;
	}

	@PostMapping("/novo")
	@Operation(summary = "Criar histórico", description = "Cria manualmente um registro de histórico (uso administrativo).")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Histórico criado",
					content = @Content(schema = @Schema(implementation = HistoricoApoiadorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<HistoricoApoiadorResponse> novo(@Valid @RequestBody HistoricoApoiadorRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(historicoApoiadorService.criar(request));
	}

	@GetMapping
	@Operation(summary = "Listar histórico", description = "Lista o histórico com filtros via query params.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<HistoricoApoiadorResponse>> listar(
			@RequestParam(required = false, name = "id_apoiador") UUID idApoiador,
			@RequestParam(required = false, name = "id_usuario") UUID idUsuario,
			@RequestParam(required = false, name = "campo_alterado") String campoAlterado
	) {
		return ResponseEntity.ok(historicoApoiadorService.listar(idApoiador, idUsuario, campoAlterado));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar histórico", description = "Atualiza um registro de histórico.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Histórico atualizado",
					content = @Content(schema = @Schema(implementation = HistoricoApoiadorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Histórico não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<HistoricoApoiadorResponse> alterarDados(@Valid @RequestBody HistoricoApoiadorUpdateRequest request) {
		return ResponseEntity.ok(historicoApoiadorService.alterar(request));
	}

	@DeleteMapping("/apagar")
	@Operation(summary = "Apagar histórico", description = "Remove fisicamente um registro de histórico.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Histórico removido"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Histórico não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<Void> apagar(@RequestParam UUID id) {
		historicoApoiadorService.apagar(id);
		return ResponseEntity.noContent().build();
	}
}

package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.ApoiadorRequest;
import com.net.convertix.ramossomar.dto.request.ApoiadorUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ApoiadorResponse;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import com.net.convertix.ramossomar.service.ApoiadorService;
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
@RequestMapping("/api/v1/ramossomar/apoiadores")
@Tag(name = "Apoiadores", description = "CRUD de apoiadores com soft delete e histórico automático")
@SecurityRequirement(name = "bearer-jwt")
public class ApoiadorController {

	private final ApoiadorService apoiadorService;

	public ApoiadorController(ApoiadorService apoiadorService) {
		this.apoiadorService = apoiadorService;
	}

	@PostMapping("/novo")
	@Operation(summary = "Criar apoiador", description = "Cadastra um novo apoiador vinculado a um líder.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Apoiador criado",
					content = @Content(schema = @Schema(implementation = ApoiadorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Líder não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<ApoiadorResponse> novo(@Valid @RequestBody ApoiadorRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(apoiadorService.criar(request));
	}

	@GetMapping
	@Operation(summary = "Listar apoiadores", description = "Lista apoiadores com filtros via query params (nome, cidade, id_lider, intencao_voto, cpf).")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<ApoiadorResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String cidade,
			@RequestParam(required = false, name = "id_lider") UUID idLider,
			@RequestParam(required = false, name = "intencao_voto") IntencaoVoto intencaoVoto,
			@RequestParam(required = false) String cpf
	) {
		return ResponseEntity.ok(apoiadorService.listar(nome, cidade, idLider, intencaoVoto, cpf));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar apoiador", description = "Atualiza dados do apoiador e registra automaticamente o histórico de alterações.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Apoiador atualizado",
					content = @Content(schema = @Schema(implementation = ApoiadorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Apoiador não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<ApoiadorResponse> alterarDados(@Valid @RequestBody ApoiadorUpdateRequest request) {
		return ResponseEntity.ok(apoiadorService.alterar(request));
	}

	@DeleteMapping("/apagar")
	@Operation(summary = "Apagar apoiador", description = "Realiza soft delete preenchendo data_exclusao.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Apoiador excluído logicamente"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Apoiador não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<Void> apagar(@RequestParam UUID id) {
		apoiadorService.apagar(id);
		return ResponseEntity.noContent().build();
	}
}

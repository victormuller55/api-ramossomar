package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.LocalVotacaoRequest;
import com.net.convertix.ramossomar.dto.request.LocalVotacaoUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.dto.response.LocalVotacaoResponse;
import com.net.convertix.ramossomar.service.LocalVotacaoService;
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
@RequestMapping("/api/v1/ramossomar/locais-votacao")
@Tag(name = "Locais de Votação", description = "CRUD de locais de votação vinculados às cidades")
@SecurityRequirement(name = "bearer-jwt")
public class LocalVotacaoController {

	private final LocalVotacaoService localVotacaoService;

	public LocalVotacaoController(LocalVotacaoService localVotacaoService) {
		this.localVotacaoService = localVotacaoService;
	}

	@PostMapping("/novo")
	@Operation(summary = "Criar local de votação", description = "Cadastra um novo local de votação vinculado a uma cidade.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Local criado",
					content = @Content(schema = @Schema(implementation = LocalVotacaoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Cidade não encontrada",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<LocalVotacaoResponse> novo(@Valid @RequestBody LocalVotacaoRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(localVotacaoService.criar(request));
	}

	@GetMapping
	@Operation(
			summary = "Listar locais de votação",
			description = "Lista locais com filtros via query params (nome, id_cidade, codigo_ibge, zona_eleitoral, ativo)."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<LocalVotacaoResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false, name = "id_cidade") UUID idCidade,
			@RequestParam(required = false, name = "codigo_ibge") String codigoIbge,
			@RequestParam(required = false, name = "zona_eleitoral") String zonaEleitoral,
			@RequestParam(required = false) Boolean ativo
	) {
		return ResponseEntity.ok(localVotacaoService.listar(nome, idCidade, codigoIbge, zonaEleitoral, ativo));
	}

	@GetMapping("/por-id")
	@Operation(summary = "Buscar local por ID", description = "Retorna um local de votação pelo identificador UUID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Local encontrado",
					content = @Content(schema = @Schema(implementation = LocalVotacaoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Local não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<LocalVotacaoResponse> buscarPorId(@RequestParam UUID id) {
		return ResponseEntity.ok(localVotacaoService.buscarPorId(id));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar local de votação", description = "Atualiza os dados de um local de votação existente.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Local atualizado",
					content = @Content(schema = @Schema(implementation = LocalVotacaoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Local ou cidade não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<LocalVotacaoResponse> alterarDados(@Valid @RequestBody LocalVotacaoUpdateRequest request) {
		return ResponseEntity.ok(localVotacaoService.alterar(request));
	}

	@DeleteMapping("/apagar")
	@Operation(summary = "Apagar local de votação", description = "Inativa o local de votação (desativação lógica via campo ativo).")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Local inativado"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Local não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<Void> apagar(@RequestParam UUID id) {
		localVotacaoService.apagar(id);
		return ResponseEntity.noContent().build();
	}
}

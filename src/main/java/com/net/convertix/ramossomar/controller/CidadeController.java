package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.response.CidadeResponse;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.service.CidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ramossomar/cidades")
@Tag(name = "Cidades", description = "Consulta de cidades (municípios) importadas automaticamente")
@SecurityRequirement(name = "bearer-jwt")
public class CidadeController {

	private final CidadeService cidadeService;

	public CidadeController(CidadeService cidadeService) {
		this.cidadeService = cidadeService;
	}

	@GetMapping
	@Operation(summary = "Listar cidades", description = "Lista cidades com filtros opcionais por nome e UF.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<CidadeResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String uf
	) {
		return ResponseEntity.ok(cidadeService.listar(nome, uf));
	}

	@GetMapping("/por-id")
	@Operation(summary = "Buscar cidade por ID", description = "Retorna uma cidade pelo identificador UUID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cidade encontrada",
					content = @Content(schema = @Schema(implementation = CidadeResponse.class))),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Cidade não encontrada",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<CidadeResponse> buscarPorId(@RequestParam UUID id) {
		return ResponseEntity.ok(cidadeService.buscarPorId(id));
	}

	@GetMapping("/por-codigo-ibge")
	@Operation(summary = "Buscar cidade por código IBGE", description = "Retorna uma cidade pelo código IBGE.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cidade encontrada",
					content = @Content(schema = @Schema(implementation = CidadeResponse.class))),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Cidade não encontrada",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<CidadeResponse> buscarPorCodigoIbge(
			@RequestParam(name = "codigo_ibge") String codigoIbge
	) {
		return ResponseEntity.ok(cidadeService.buscarPorCodigoIbge(codigoIbge));
	}
}

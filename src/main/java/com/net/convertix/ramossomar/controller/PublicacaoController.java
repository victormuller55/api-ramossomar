package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.PublicacaoRequest;
import com.net.convertix.ramossomar.dto.request.PublicacaoUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.dto.response.PublicacaoResponse;
import com.net.convertix.ramossomar.service.PublicacaoService;
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
@RequestMapping("/api/v1/ramossomar/publicacoes")
@Tag(name = "Publicações", description = "CRUD de publicações com mídia")
@SecurityRequirement(name = "bearer-jwt")
public class PublicacaoController {

	private final PublicacaoService publicacaoService;

	public PublicacaoController(PublicacaoService publicacaoService) {
		this.publicacaoService = publicacaoService;
	}

	@PostMapping("/novo")
	@Operation(summary = "Criar publicação", description = "Cadastra uma nova publicação com imagem ou vídeo.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Publicação criada",
					content = @Content(schema = @Schema(implementation = PublicacaoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Autor não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<PublicacaoResponse> novo(@Valid @RequestBody PublicacaoRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(publicacaoService.criar(request));
	}

	@GetMapping
	@Operation(summary = "Listar publicações", description = "Lista publicações com filtros via query params.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<PublicacaoResponse>> listar(
			@RequestParam(required = false) String titulo,
			@RequestParam(required = false, name = "id_autor") UUID idAutor
	) {
		return ResponseEntity.ok(publicacaoService.listar(titulo, idAutor));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar publicação", description = "Atualiza os dados de uma publicação existente.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Publicação atualizada",
					content = @Content(schema = @Schema(implementation = PublicacaoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Publicação não encontrada",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<PublicacaoResponse> alterarDados(@Valid @RequestBody PublicacaoUpdateRequest request) {
		return ResponseEntity.ok(publicacaoService.alterar(request));
	}

	@DeleteMapping("/apagar")
	@Operation(summary = "Apagar publicação", description = "Remove fisicamente uma publicação.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Publicação removida"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Publicação não encontrada",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<Void> apagar(@RequestParam UUID id) {
		publicacaoService.apagar(id);
		return ResponseEntity.noContent().build();
	}
}

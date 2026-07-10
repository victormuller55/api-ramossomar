package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.UsuarioRequest;
import com.net.convertix.ramossomar.dto.request.UsuarioUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.dto.response.UsuarioResponse;
import com.net.convertix.ramossomar.model.enums.Perfil;
import com.net.convertix.ramossomar.service.UsuarioService;
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
@RequestMapping("/api/v1/ramossomar/usuarios")
@Tag(name = "Usuários", description = "CRUD de usuários do sistema")
@SecurityRequirement(name = "bearer-jwt")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping("/novo")
	@Operation(summary = "Criar usuário", description = "Cadastra um novo usuário com senha criptografada em BCrypt.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Usuário criado",
					content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<UsuarioResponse> novo(@Valid @RequestBody UsuarioRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));
	}

	@GetMapping
	@Operation(summary = "Listar usuários", description = "Lista usuários com filtros opcionais via query params.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista retornada"),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<List<UsuarioResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) Perfil perfil,
			@RequestParam(required = false) Boolean ativo
	) {
		return ResponseEntity.ok(usuarioService.listar(nome, email, perfil, ativo));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar usuário", description = "Atualiza os dados de um usuário existente.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Usuário atualizado",
					content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<UsuarioResponse> alterarDados(@Valid @RequestBody UsuarioUpdateRequest request) {
		return ResponseEntity.ok(usuarioService.alterar(request));
	}

	@DeleteMapping("/apagar")
	@Operation(summary = "Apagar usuário", description = "Inativa o usuário (desativação lógica).")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Usuário inativado"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<Void> apagar(@RequestParam UUID id) {
		usuarioService.apagar(id);
		return ResponseEntity.noContent().build();
	}
}

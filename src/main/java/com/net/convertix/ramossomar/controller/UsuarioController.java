package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.request.UsuarioRequest;
import com.net.convertix.ramossomar.dto.request.UsuarioUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.dto.response.PaginacaoResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar usuário", description = "Cadastra um novo usuário com senha criptografada em BCrypt. A imagem de perfil é enviada depois via upload.")
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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(
			summary = "Listar usuários",
			description = "Lista usuários com paginação (20 itens por página) e filtros opcionais via query params (nome, email, perfil, ativo, pagina)."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista paginada retornada",
					content = @Content(schema = @Schema(implementation = PaginacaoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<PaginacaoResponse<UsuarioResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) Perfil perfil,
			@RequestParam(required = false) Boolean ativo,
			@RequestParam(required = false, defaultValue = "1") Integer pagina
	) {
		return ResponseEntity.ok(usuarioService.listar(nome, email, perfil, ativo, pagina));
	}

	@PutMapping("/alterar-dados")
	@Operation(summary = "Alterar usuário", description = "Atualiza os dados de um usuário existente (sem alterar a imagem de perfil).")
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

	@PostMapping(value = "/upload-imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
			summary = "Upload de imagem de perfil",
			description = "Envia um arquivo de imagem (JPG, PNG, WEBP ou GIF) para o perfil do usuário. Salva no servidor e atualiza o campo imagem."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Imagem atualizada",
					content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Arquivo inválido",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<UsuarioResponse> uploadImagem(
			@RequestParam UUID id,
			@RequestParam("imagem") MultipartFile imagem
	) {
		return ResponseEntity.ok(usuarioService.uploadImagemPerfil(id, imagem));
	}

	@DeleteMapping("/apagar")
	@PreAuthorize("hasRole('ADMIN')")
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

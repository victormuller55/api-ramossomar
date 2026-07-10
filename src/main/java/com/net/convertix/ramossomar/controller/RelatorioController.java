package com.net.convertix.ramossomar.controller;

import com.net.convertix.ramossomar.dto.response.ErroResponse;
import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import com.net.convertix.ramossomar.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ramossomar/relatorios")
@Tag(name = "Relatórios", description = "Exportação de cadastrados em XLSX e PDF")
@SecurityRequirement(name = "bearer-jwt")
public class RelatorioController {

	private static final DateTimeFormatter ARQUIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

	private final RelatorioService relatorioService;

	public RelatorioController(RelatorioService relatorioService) {
		this.relatorioService = relatorioService;
	}

	@GetMapping("/apoiadores")
	@Operation(
			summary = "Exportar cadastrados",
			description = "Gera arquivo XLSX ou PDF com a lista de apoiadores. Somente ADMIN."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Arquivo gerado"),
			@ApiResponse(responseCode = "400", description = "Formato ou parâmetros inválidos",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "403", description = "Acesso negado",
					content = @Content(schema = @Schema(implementation = ErroResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno",
					content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	})
	public ResponseEntity<byte[]> exportarApoiadores(
			@RequestParam String formato,
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String cidade,
			@RequestParam(required = false, name = "id_lider") UUID idLider,
			@RequestParam(required = false, name = "intencao_voto") IntencaoVoto intencaoVoto,
			@RequestParam(required = false) String cpf
	) {
		byte[] arquivo = relatorioService.exportarApoiadores(
				formato, nome, cidade, idLider, intencaoVoto, cpf
		);

		boolean pdf = "pdf".equalsIgnoreCase(formato);
		String extensao = pdf ? "pdf" : "xlsx";
		String filename = "cadastrados_" + LocalDateTime.now().format(ARQUIVO) + "." + extensao;
		MediaType mediaType = pdf
				? MediaType.APPLICATION_PDF
				: MediaType.parseMediaType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
				);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(mediaType)
				.body(arquivo);
	}
}

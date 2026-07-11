package com.net.convertix.ramossomar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.convertix.ramossomar.config.ImportacaoProperties;
import com.net.convertix.ramossomar.model.Cidade;
import com.net.convertix.ramossomar.model.LocalVotacao;
import com.net.convertix.ramossomar.repository.CidadeRepository;
import com.net.convertix.ramossomar.repository.LocalVotacaoRepository;
import com.net.convertix.ramossomar.util.TextoUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CidadeLocalVotacaoImportService {

	private static final Logger log = LoggerFactory.getLogger(CidadeLocalVotacaoImportService.class);
	private static final Charset TSE_CHARSET = Charset.forName("ISO-8859-1");

	private final CidadeRepository cidadeRepository;
	private final LocalVotacaoRepository localVotacaoRepository;
	private final ImportacaoProperties properties;
	private final TransactionTemplate transactionTemplate;
	private final ObjectMapper objectMapper;
	private final HttpClient httpClient;

	public CidadeLocalVotacaoImportService(
			CidadeRepository cidadeRepository,
			LocalVotacaoRepository localVotacaoRepository,
			ImportacaoProperties properties,
			PlatformTransactionManager transactionManager
	) {
		this.cidadeRepository = cidadeRepository;
		this.localVotacaoRepository = localVotacaoRepository;
		this.properties = properties;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		this.objectMapper = new ObjectMapper();
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(30))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
	}

	public void importarCidadesSeNecessario() throws Exception {
		if (cidadeRepository.count() > 0) {
			log.info("Importação de cidades ignorada: tbl_cidades já possui registros");
			return;
		}

		long inicio = System.currentTimeMillis();
		String uf = properties.getUf().toUpperCase();
		String url = properties.getIbge().resolverUrl(uf);
		log.info("Iniciando importação de municípios do IBGE para UF={} a partir de {}", uf, url);

		List<Map<String, Object>> municipios = baixarMunicipiosIbge(url);
		List<Cidade> cidades = new ArrayList<>();

		for (Map<String, Object> municipio : municipios) {
			Object id = municipio.get("id");
			Object nome = municipio.get("nome");
			if (id == null || nome == null) {
				continue;
			}

			Cidade cidade = new Cidade();
			cidade.setCodigoIbge(String.valueOf(id));
			cidade.setNome(String.valueOf(nome).trim());
			cidade.setUf(uf);
			cidades.add(cidade);
		}

		if (cidades.isEmpty()) {
			throw new IllegalStateException("Nenhum município foi retornado pela API do IBGE");
		}

		transactionTemplate.executeWithoutResult(status -> persistirCidades(cidades));

		long tempoMs = System.currentTimeMillis() - inicio;
		log.info(
				"Importação de cidades concluída: cadastradas={}, uf={}, tempo_ms={}",
				cidades.size(),
				uf,
				tempoMs
		);
	}

	public void importarLocaisVotacaoSeNecessario() throws Exception {
		if (localVotacaoRepository.count() > 0) {
			log.info("Importação de locais de votação ignorada: tbl_locais_votacao já possui registros");
			return;
		}

		if (cidadeRepository.count() == 0) {
			log.warn("Importação de locais de votação abortada: não há cidades cadastradas");
			return;
		}

		long inicio = System.currentTimeMillis();
		String uf = properties.getUf().toUpperCase();
		String url = properties.getTse().getLocaisVotacaoUrl();
		log.info("Iniciando download dos locais de votação do TSE: {}", url);

		Path zipTemporario = Files.createTempFile("eleitorado_local_votacao_", ".zip");
		try {
			baixarArquivo(url, zipTemporario);
			Map<String, LocalTseDto> locaisUnicos = processarZipTse(zipTemporario, uf);
			ResultadoImportacao resultado = transactionTemplate.execute(status -> persistirLocais(locaisUnicos, uf));
			if (resultado == null) {
				throw new IllegalStateException("A persistência dos locais de votação não retornou resultado");
			}

			long tempoMs = System.currentTimeMillis() - inicio;
			log.info(
					"Importação de locais de votação concluída: processados={}, inseridos={}, atualizados={}, ignorados={}, tempo_ms={}",
					resultado.processados(),
					resultado.inseridos(),
					resultado.atualizados(),
					resultado.ignorados(),
					tempoMs
			);
		} finally {
			Files.deleteIfExists(zipTemporario);
		}
	}

	private void persistirCidades(List<Cidade> cidades) {
		int batchSize = properties.getBatchSize();
		for (int i = 0; i < cidades.size(); i += batchSize) {
			List<Cidade> fatia = cidades.subList(i, Math.min(i + batchSize, cidades.size()));
			cidadeRepository.saveAll(new ArrayList<>(fatia));
			cidadeRepository.flush();
		}
	}

	private ResultadoImportacao persistirLocais(Map<String, LocalTseDto> locaisUnicos, String uf) {
		Map<String, Cidade> cidadesPorNome = new HashMap<>();
		for (Cidade cidade : cidadeRepository.findByUfOrderByNomeAsc(uf)) {
			cidadesPorNome.put(TextoUtil.normalizar(cidade.getNome()), cidade);
		}

		Map<String, LocalVotacao> existentesPorCodigo = new HashMap<>();
		List<String> codigos = new ArrayList<>(locaisUnicos.keySet());
		int consultaBatch = Math.max(100, properties.getBatchSize());
		for (int i = 0; i < codigos.size(); i += consultaBatch) {
			List<String> fatia = codigos.subList(i, Math.min(i + consultaBatch, codigos.size()));
			for (LocalVotacao existente : localVotacaoRepository.findByCodigoTseIn(fatia)) {
				existentesPorCodigo.put(existente.getCodigoTse(), existente);
			}
		}

		int processados = 0;
		int inseridos = 0;
		int atualizados = 0;
		int ignorados = 0;
		List<LocalVotacao> lote = new ArrayList<>();

		for (LocalTseDto dto : locaisUnicos.values()) {
			processados++;
			Cidade cidade = cidadesPorNome.get(dto.nomeMunicipioNormalizado);
			if (cidade == null) {
				ignorados++;
				log.warn(
						"Local TSE {} ignorado: cidade não encontrada para município '{}'",
						dto.codigoTse,
						dto.nomeMunicipioNormalizado
				);
				continue;
			}

			LocalVotacao existente = existentesPorCodigo.get(dto.codigoTse);
			if (existente == null) {
				LocalVotacao novo = new LocalVotacao();
				aplicarDados(novo, dto, cidade);
				lote.add(novo);
				inseridos++;
			} else if (precisaAtualizar(existente, dto, cidade)) {
				aplicarDados(existente, dto, cidade);
				lote.add(existente);
				atualizados++;
			} else {
				ignorados++;
			}

			if (lote.size() >= properties.getBatchSize()) {
				localVotacaoRepository.saveAll(lote);
				localVotacaoRepository.flush();
				lote.clear();
			}
		}

		if (!lote.isEmpty()) {
			localVotacaoRepository.saveAll(lote);
			localVotacaoRepository.flush();
		}

		return new ResultadoImportacao(processados, inseridos, atualizados, ignorados);
	}

	private List<Map<String, Object>> baixarMunicipiosIbge(String url) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.timeout(Duration.ofMinutes(2))
				.GET()
				.header("Accept", "application/json")
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IllegalStateException("Falha ao consultar IBGE. HTTP " + response.statusCode());
		}

		return objectMapper.readValue(response.body(), new TypeReference<>() {
		});
	}

	private void baixarArquivo(String url, Path destino) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.timeout(Duration.ofMinutes(10))
				.GET()
				.build();

		HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(destino));
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IllegalStateException("Falha no download do arquivo TSE. HTTP " + response.statusCode());
		}
		log.info("Download TSE concluído: {} bytes", Files.size(destino));
	}

	private Map<String, LocalTseDto> processarZipTse(Path zipTemporario, String uf) throws Exception {
		Map<String, LocalTseDto> locaisUnicos = new LinkedHashMap<>();

		try (InputStream fileIn = Files.newInputStream(zipTemporario);
				ZipInputStream zipIn = new ZipInputStream(fileIn)) {

			ZipEntry entry;
			boolean csvEncontrado = false;
			while ((entry = zipIn.getNextEntry()) != null) {
				if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith(".csv")) {
					continue;
				}
				csvEncontrado = true;
				log.info("Processando arquivo CSV do TSE: {}", entry.getName());
				processarCsvTse(zipIn, uf, locaisUnicos);
				break;
			}

			if (!csvEncontrado) {
				throw new IllegalStateException("Nenhum arquivo CSV encontrado no ZIP do TSE");
			}
		}

		log.info("Locais únicos extraídos para UF={}: {}", uf, locaisUnicos.size());
		return locaisUnicos;
	}

	private void processarCsvTse(InputStream csvStream, String uf, Map<String, LocalTseDto> locaisUnicos) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream, TSE_CHARSET));
		String cabecalho = reader.readLine();
		if (cabecalho == null) {
			throw new IllegalStateException("CSV do TSE está vazio");
		}

		Map<String, Integer> indices = mapearCabecalho(cabecalho);
		validarColunasObrigatorias(indices);

		String linha;
		long linhasLidas = 0;
		while ((linha = reader.readLine()) != null) {
			linhasLidas++;
			List<String> colunas = parsearLinhaCsv(linha);
			if (colunas.size() <= indices.get("NR_LOCAL_VOTACAO")) {
				continue;
			}

			String sgUf = valor(colunas, indices, "SG_UF");
			if (!uf.equalsIgnoreCase(sgUf)) {
				continue;
			}

			String codigoMunicipio = valor(colunas, indices, "CD_MUNICIPIO");
			String nomeMunicipio = valor(colunas, indices, "NM_MUNICIPIO");
			String zona = valor(colunas, indices, "NR_ZONA");
			String numeroLocal = valor(colunas, indices, "NR_LOCAL_VOTACAO");
			String nomeLocal = valor(colunas, indices, "NM_LOCAL_VOTACAO");

			if (TextoUtil.isValorInvalidoTse(codigoMunicipio)
					|| TextoUtil.isValorInvalidoTse(zona)
					|| TextoUtil.isValorInvalidoTse(numeroLocal)
					|| TextoUtil.isValorInvalidoTse(nomeLocal)
					|| TextoUtil.isValorInvalidoTse(nomeMunicipio)) {
				continue;
			}

			String codigoTse = codigoMunicipio.trim() + "-" + zona.trim() + "-" + numeroLocal.trim();
			if (locaisUnicos.containsKey(codigoTse)) {
				continue;
			}

			LocalTseDto dto = new LocalTseDto();
			dto.codigoTse = codigoTse;
			dto.nome = nomeLocal.trim();
			dto.endereco = Objects.requireNonNullElse(
					TextoUtil.limparValorTse(valor(colunas, indices, "DS_ENDERECO")),
					"NÃO INFORMADO"
			);
			dto.bairro = TextoUtil.limparValorTse(valor(colunas, indices, "NM_BAIRRO"));
			dto.cep = TextoUtil.limparValorTse(valor(colunas, indices, "NR_CEP"));
			dto.zonaEleitoral = zona.trim();
			dto.latitude = parseDecimal(valor(colunas, indices, "NR_LATITUDE"));
			dto.longitude = parseDecimal(valor(colunas, indices, "NR_LONGITUDE"));
			dto.ativo = isLocalAtivo(
					valor(colunas, indices, "CD_SITU_LOCAL_VOTACAO"),
					valor(colunas, indices, "DS_SITU_LOCAL_VOTACAO")
			);
			dto.nomeMunicipioNormalizado = TextoUtil.normalizar(nomeMunicipio);
			locaisUnicos.put(codigoTse, dto);

			if (linhasLidas % 100_000 == 0) {
				log.info(
						"Progresso leitura CSV TSE: {} linhas processadas, {} locais únicos {}",
						linhasLidas,
						locaisUnicos.size(),
						uf
				);
			}
		}

		log.info("Leitura CSV TSE finalizada: {} linhas lidas", linhasLidas);
	}

	private void aplicarDados(LocalVotacao local, LocalTseDto dto, Cidade cidade) {
		local.setCodigoTse(dto.codigoTse);
		local.setNome(dto.nome);
		local.setEndereco(dto.endereco);
		local.setBairro(dto.bairro);
		local.setCep(dto.cep);
		local.setZonaEleitoral(dto.zonaEleitoral);
		local.setLatitude(dto.latitude);
		local.setLongitude(dto.longitude);
		local.setAtivo(dto.ativo);
		local.setCidade(cidade);
	}

	private boolean precisaAtualizar(LocalVotacao existente, LocalTseDto dto, Cidade cidade) {
		return !Objects.equals(existente.getNome(), dto.nome)
				|| !Objects.equals(existente.getEndereco(), dto.endereco)
				|| !Objects.equals(existente.getBairro(), dto.bairro)
				|| !Objects.equals(existente.getCep(), dto.cep)
				|| !Objects.equals(existente.getZonaEleitoral(), dto.zonaEleitoral)
				|| !Objects.equals(existente.getLatitude(), dto.latitude)
				|| !Objects.equals(existente.getLongitude(), dto.longitude)
				|| !Objects.equals(existente.getAtivo(), dto.ativo)
				|| !Objects.equals(existente.getCidade().getId(), cidade.getId());
	}

	private Map<String, Integer> mapearCabecalho(String cabecalho) {
		List<String> colunas = parsearLinhaCsv(cabecalho);
		Map<String, Integer> indices = new HashMap<>();
		for (int i = 0; i < colunas.size(); i++) {
			indices.put(colunas.get(i).trim().toUpperCase(), i);
		}
		return indices;
	}

	private void validarColunasObrigatorias(Map<String, Integer> indices) {
		List<String> obrigatorias = List.of(
				"SG_UF", "CD_MUNICIPIO", "NM_MUNICIPIO", "NR_ZONA",
				"NR_LOCAL_VOTACAO", "NM_LOCAL_VOTACAO", "DS_ENDERECO"
		);
		for (String coluna : obrigatorias) {
			if (!indices.containsKey(coluna)) {
				throw new IllegalStateException("Coluna obrigatória ausente no CSV do TSE: " + coluna);
			}
		}
	}

	private List<String> parsearLinhaCsv(String linha) {
		List<String> valores = new ArrayList<>();
		StringBuilder atual = new StringBuilder();
		boolean entreAspas = false;

		for (int i = 0; i < linha.length(); i++) {
			char c = linha.charAt(i);
			if (c == '"') {
				if (entreAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
					atual.append('"');
					i++;
				} else {
					entreAspas = !entreAspas;
				}
			} else if (c == ';' && !entreAspas) {
				valores.add(atual.toString());
				atual.setLength(0);
			} else {
				atual.append(c);
			}
		}
		valores.add(atual.toString());
		return valores;
	}

	private String valor(List<String> colunas, Map<String, Integer> indices, String nome) {
		Integer indice = indices.get(nome);
		if (indice == null || indice >= colunas.size()) {
			return null;
		}
		return colunas.get(indice);
	}

	private BigDecimal parseDecimal(String valor) {
		String limpo = TextoUtil.limparValorTse(valor);
		if (limpo == null) {
			return null;
		}
		try {
			return new BigDecimal(limpo.replace(',', '.'));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private boolean isLocalAtivo(String codigoSituacao, String descricaoSituacao) {
		if ("1".equals(TextoUtil.limparValorTse(codigoSituacao))) {
			return true;
		}
		String descricao = TextoUtil.limparValorTse(descricaoSituacao);
		return descricao != null && descricao.equalsIgnoreCase("ATIVO");
	}

	private static final class LocalTseDto {
		private String codigoTse;
		private String nome;
		private String endereco;
		private String bairro;
		private String cep;
		private String zonaEleitoral;
		private BigDecimal latitude;
		private BigDecimal longitude;
		private Boolean ativo;
		private String nomeMunicipioNormalizado;
	}

	private record ResultadoImportacao(int processados, int inseridos, int atualizados, int ignorados) {
	}
}

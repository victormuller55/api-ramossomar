package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.LocalVotacaoRequest;
import com.net.convertix.ramossomar.dto.request.LocalVotacaoUpdateRequest;
import com.net.convertix.ramossomar.dto.response.LocalVotacaoResponse;
import com.net.convertix.ramossomar.exception.NegocioException;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Cidade;
import com.net.convertix.ramossomar.model.LocalVotacao;
import com.net.convertix.ramossomar.repository.LocalVotacaoRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalVotacaoService {

	private final LocalVotacaoRepository localVotacaoRepository;
	private final CidadeService cidadeService;
	private final SegurancaUtil segurancaUtil;

	public LocalVotacaoService(
			LocalVotacaoRepository localVotacaoRepository,
			CidadeService cidadeService,
			SegurancaUtil segurancaUtil
	) {
		this.localVotacaoRepository = localVotacaoRepository;
		this.cidadeService = cidadeService;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional
	public LocalVotacaoResponse criar(LocalVotacaoRequest request) {
		segurancaUtil.obterUsuarioAutenticado();

		if (localVotacaoRepository.existsByCodigoTse(request.getCodigo_tse())) {
			throw new NegocioException("Já existe um local de votação com este código TSE");
		}

		Cidade cidade = cidadeService.obterPorId(request.getId_cidade());

		LocalVotacao local = new LocalVotacao();
		local.setCodigoTse(request.getCodigo_tse().trim());
		local.setNome(request.getNome().trim());
		local.setEndereco(request.getEndereco().trim());
		local.setBairro(limparOpcional(request.getBairro()));
		local.setCep(limparOpcional(request.getCep()));
		local.setZonaEleitoral(request.getZona_eleitoral().trim());
		local.setLatitude(request.getLatitude());
		local.setLongitude(request.getLongitude());
		local.setAtivo(request.getAtivo() == null || request.getAtivo());
		local.setCidade(cidade);

		return MapperUtil.paraLocalVotacaoResponse(localVotacaoRepository.save(local));
	}

	@Transactional(readOnly = true)
	public List<LocalVotacaoResponse> listar(
			String nome,
			UUID idCidade,
			String codigoIbge,
			String zonaEleitoral,
			Boolean ativo
	) {
		segurancaUtil.obterUsuarioAutenticado();
		return localVotacaoRepository.filtrar(nome, idCidade, codigoIbge, zonaEleitoral, ativo).stream()
				.map(MapperUtil::paraLocalVotacaoResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public LocalVotacaoResponse buscarPorId(UUID id) {
		segurancaUtil.obterUsuarioAutenticado();
		return MapperUtil.paraLocalVotacaoResponse(obterPorId(id));
	}

	@Transactional
	public LocalVotacaoResponse alterar(LocalVotacaoUpdateRequest request) {
		segurancaUtil.obterUsuarioAutenticado();
		LocalVotacao local = obterPorId(request.getId());

		localVotacaoRepository.findByCodigoTse(request.getCodigo_tse())
				.filter(existente -> !existente.getId().equals(request.getId()))
				.ifPresent(existente -> {
					throw new NegocioException("Já existe um local de votação com este código TSE");
				});

		Cidade cidade = cidadeService.obterPorId(request.getId_cidade());

		local.setCodigoTse(request.getCodigo_tse().trim());
		local.setNome(request.getNome().trim());
		local.setEndereco(request.getEndereco().trim());
		local.setBairro(limparOpcional(request.getBairro()));
		local.setCep(limparOpcional(request.getCep()));
		local.setZonaEleitoral(request.getZona_eleitoral().trim());
		local.setLatitude(request.getLatitude());
		local.setLongitude(request.getLongitude());
		local.setAtivo(request.getAtivo());
		local.setCidade(cidade);

		return MapperUtil.paraLocalVotacaoResponse(localVotacaoRepository.save(local));
	}

	@Transactional
	public void apagar(UUID id) {
		segurancaUtil.obterUsuarioAutenticado();
		LocalVotacao local = obterPorId(id);
		local.setAtivo(false);
		localVotacaoRepository.save(local);
	}

	private LocalVotacao obterPorId(UUID id) {
		return localVotacaoRepository.buscarPorId(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Local de votação não encontrado"));
	}

	private String limparOpcional(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}
}

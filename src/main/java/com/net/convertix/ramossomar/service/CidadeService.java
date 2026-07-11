package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.response.CidadeResponse;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Cidade;
import com.net.convertix.ramossomar.repository.CidadeRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CidadeService {

	private final CidadeRepository cidadeRepository;
	private final SegurancaUtil segurancaUtil;

	public CidadeService(CidadeRepository cidadeRepository, SegurancaUtil segurancaUtil) {
		this.cidadeRepository = cidadeRepository;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional(readOnly = true)
	public List<CidadeResponse> listar(String nome, String uf) {
		segurancaUtil.obterUsuarioAutenticado();
		return cidadeRepository.filtrar(nome, uf).stream()
				.map(MapperUtil::paraCidadeResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public CidadeResponse buscarPorId(UUID id) {
		segurancaUtil.obterUsuarioAutenticado();
		return MapperUtil.paraCidadeResponse(obterPorId(id));
	}

	@Transactional(readOnly = true)
	public CidadeResponse buscarPorCodigoIbge(String codigoIbge) {
		segurancaUtil.obterUsuarioAutenticado();
		Cidade cidade = cidadeRepository.findByCodigoIbge(codigoIbge)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Cidade não encontrada para o código IBGE informado"));
		return MapperUtil.paraCidadeResponse(cidade);
	}

	@Transactional(readOnly = true)
	public Cidade obterPorId(UUID id) {
		return cidadeRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Cidade não encontrada"));
	}
}

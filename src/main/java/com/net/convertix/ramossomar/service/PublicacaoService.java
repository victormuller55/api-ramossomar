package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.PublicacaoRequest;
import com.net.convertix.ramossomar.dto.request.PublicacaoUpdateRequest;
import com.net.convertix.ramossomar.dto.response.PublicacaoResponse;
import com.net.convertix.ramossomar.exception.AcessoNegadoException;
import com.net.convertix.ramossomar.exception.NegocioException;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Publicacao;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.repository.PublicacaoRepository;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.security.UsuarioAutenticado;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicacaoService {

	private final PublicacaoRepository publicacaoRepository;
	private final UsuarioRepository usuarioRepository;
	private final SegurancaUtil segurancaUtil;

	public PublicacaoService(
			PublicacaoRepository publicacaoRepository,
			UsuarioRepository usuarioRepository,
			SegurancaUtil segurancaUtil
	) {
		this.publicacaoRepository = publicacaoRepository;
		this.usuarioRepository = usuarioRepository;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional
	public PublicacaoResponse criar(PublicacaoRequest request) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		validarAutor(request.getId_autor(), autenticado);
		validarMidia(request.getMidia(), request.getTipo_midia());

		Usuario autor = buscarAutor(request.getId_autor());

		Publicacao publicacao = new Publicacao();
		publicacao.setAutor(autor);
		publicacao.setTitulo(request.getTitulo());
		publicacao.setConteudo(request.getConteudo());
		publicacao.setMidia(request.getMidia());
		publicacao.setTipoMidia(request.getTipo_midia());

		return MapperUtil.paraPublicacaoResponse(publicacaoRepository.save(publicacao));
	}

	@Transactional(readOnly = true)
	public List<PublicacaoResponse> listar(String titulo, UUID idAutor) {
		segurancaUtil.obterUsuarioAutenticado();
		return publicacaoRepository.filtrar(titulo, idAutor).stream()
				.map(MapperUtil::paraPublicacaoResponse)
				.toList();
	}

	@Transactional
	public PublicacaoResponse alterar(PublicacaoUpdateRequest request) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		Publicacao publicacao = buscarPorId(request.getId());
		validarAutor(publicacao.getAutor().getId(), autenticado);
		validarAutor(request.getId_autor(), autenticado);
		validarMidia(request.getMidia(), request.getTipo_midia());

		Usuario autor = buscarAutor(request.getId_autor());
		publicacao.setAutor(autor);
		publicacao.setTitulo(request.getTitulo());
		publicacao.setConteudo(request.getConteudo());
		publicacao.setMidia(request.getMidia());
		publicacao.setTipoMidia(request.getTipo_midia());

		return MapperUtil.paraPublicacaoResponse(publicacaoRepository.save(publicacao));
	}

	@Transactional
	public void apagar(UUID id) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		Publicacao publicacao = buscarPorId(id);
		validarAutor(publicacao.getAutor().getId(), autenticado);
		publicacaoRepository.delete(publicacao);
	}

	private void validarAutor(UUID idAutor, UsuarioAutenticado autenticado) {
		if (autenticado.isAdmin()) {
			return;
		}
		if (!autenticado.getId().equals(idAutor)) {
			throw new AcessoNegadoException("Você só pode gerenciar suas próprias publicações");
		}
	}

	private void validarMidia(String midia, Object tipoMidia) {
		if (midia != null && !midia.isBlank() && tipoMidia == null) {
			throw new NegocioException("Informe o tipo_midia quando houver mídia");
		}
		if ((midia == null || midia.isBlank()) && tipoMidia != null) {
			throw new NegocioException("Informe a mídia quando houver tipo_midia");
		}
	}

	private Usuario buscarAutor(UUID idAutor) {
		return usuarioRepository.findById(idAutor)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Autor não encontrado"));
	}

	private Publicacao buscarPorId(UUID id) {
		return publicacaoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Publicação não encontrada"));
	}
}

package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.exception.AcessoNegadoException;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Apoiador;
import com.net.convertix.ramossomar.model.Publicacao;
import com.net.convertix.ramossomar.repository.ApoiadorRepository;
import com.net.convertix.ramossomar.repository.PublicacaoRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.security.UsuarioAutenticado;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Autorização de domínio: ADMIN bypassa escopo; LIDER só acessa o próprio escopo.
 */
@Service
public class AutorizacaoService {

	private final SegurancaUtil segurancaUtil;
	private final ApoiadorRepository apoiadorRepository;
	private final PublicacaoRepository publicacaoRepository;

	public AutorizacaoService(
			SegurancaUtil segurancaUtil,
			ApoiadorRepository apoiadorRepository,
			PublicacaoRepository publicacaoRepository
	) {
		this.segurancaUtil = segurancaUtil;
		this.apoiadorRepository = apoiadorRepository;
		this.publicacaoRepository = publicacaoRepository;
	}

	public void exigirAdmin() {
		segurancaUtil.exigirAdmin();
	}

	/**
	 * Admin: null (sem filtro). Líder: próprio id.
	 */
	public UUID resolverLiderIdFiltro() {
		UsuarioAutenticado usuario = segurancaUtil.obterUsuarioAutenticado();
		if (usuario.isAdmin()) {
			return null;
		}
		return usuario.getId();
	}

	/**
	 * Admin pode informar qualquer líder; líder é forçado ao próprio id.
	 */
	public UUID resolverLiderId(UUID liderIdInformado) {
		UsuarioAutenticado usuario = segurancaUtil.obterUsuarioAutenticado();
		if (usuario.isAdmin()) {
			return liderIdInformado;
		}
		return usuario.getId();
	}

	public void forcarLiderId(UUID liderIdInformado) {
		UsuarioAutenticado usuario = segurancaUtil.obterUsuarioAutenticado();
		if (usuario.isAdmin()) {
			return;
		}
		if (liderIdInformado != null && !liderIdInformado.equals(usuario.getId())) {
			throw new AcessoNegadoException("Acesso negado a este recurso");
		}
	}

	public void validarAcessoRecursoUsuario(UUID idUsuario) {
		segurancaUtil.exigirAdminOuProprioUsuario(idUsuario);
	}

	public void validarAcessoLider(UUID idLider) {
		segurancaUtil.exigirAdminOuLiderDono(idLider);
	}

	@Transactional(readOnly = true)
	public void validarAcessoApoiador(UUID apoiadorId) {
		UsuarioAutenticado usuario = segurancaUtil.obterUsuarioAutenticado();
		if (usuario.isAdmin()) {
			return;
		}
		Apoiador apoiador = apoiadorRepository.findById(apoiadorId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Apoiador não encontrado"));
		if (!apoiador.getLider().getId().equals(usuario.getId())) {
			throw new AcessoNegadoException("Acesso negado a este recurso");
		}
	}

	@Transactional(readOnly = true)
	public void validarAcessoPublicacao(UUID publicacaoId) {
		UsuarioAutenticado usuario = segurancaUtil.obterUsuarioAutenticado();
		if (usuario.isAdmin()) {
			return;
		}
		Publicacao publicacao = publicacaoRepository.findById(publicacaoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Publicação não encontrada"));
		if (publicacao.getAutor() == null || !publicacao.getAutor().getId().equals(usuario.getId())) {
			throw new AcessoNegadoException("Acesso negado a este recurso");
		}
	}
}

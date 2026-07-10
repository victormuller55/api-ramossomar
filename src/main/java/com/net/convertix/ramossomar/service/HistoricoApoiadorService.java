package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.HistoricoApoiadorRequest;
import com.net.convertix.ramossomar.dto.request.HistoricoApoiadorUpdateRequest;
import com.net.convertix.ramossomar.dto.response.HistoricoApoiadorResponse;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Apoiador;
import com.net.convertix.ramossomar.model.HistoricoApoiador;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.repository.ApoiadorRepository;
import com.net.convertix.ramossomar.repository.HistoricoApoiadorRepository;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.security.UsuarioAutenticado;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoricoApoiadorService {

	private final HistoricoApoiadorRepository historicoApoiadorRepository;
	private final ApoiadorRepository apoiadorRepository;
	private final UsuarioRepository usuarioRepository;
	private final SegurancaUtil segurancaUtil;

	public HistoricoApoiadorService(
			HistoricoApoiadorRepository historicoApoiadorRepository,
			ApoiadorRepository apoiadorRepository,
			UsuarioRepository usuarioRepository,
			SegurancaUtil segurancaUtil
	) {
		this.historicoApoiadorRepository = historicoApoiadorRepository;
		this.apoiadorRepository = apoiadorRepository;
		this.usuarioRepository = usuarioRepository;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional
	public HistoricoApoiadorResponse criar(HistoricoApoiadorRequest request) {
		segurancaUtil.exigirAdmin();

		Apoiador apoiador = apoiadorRepository.findById(request.getId_apoiador())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Apoiador não encontrado"));
		Usuario usuario = usuarioRepository.findById(request.getId_usuario())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

		HistoricoApoiador historico = new HistoricoApoiador();
		historico.setApoiador(apoiador);
		historico.setUsuario(usuario);
		historico.setCampoAlterado(request.getCampo_alterado());
		historico.setValorAnterior(request.getValor_anterior());
		historico.setValorNovo(request.getValor_novo());

		return MapperUtil.paraHistoricoResponse(historicoApoiadorRepository.save(historico));
	}

	@Transactional(readOnly = true)
	public List<HistoricoApoiadorResponse> listar(UUID idApoiador, UUID idUsuario, String campoAlterado) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();

		UUID filtroUsuario = idUsuario;
		if (!autenticado.isAdmin()) {
			filtroUsuario = autenticado.getId();
		}

		return historicoApoiadorRepository.filtrar(idApoiador, filtroUsuario, campoAlterado).stream()
				.filter(h -> autenticado.isAdmin() || h.getApoiador().getLider().getId().equals(autenticado.getId()))
				.map(MapperUtil::paraHistoricoResponse)
				.toList();
	}

	@Transactional
	public HistoricoApoiadorResponse alterar(HistoricoApoiadorUpdateRequest request) {
		segurancaUtil.exigirAdmin();

		HistoricoApoiador historico = historicoApoiadorRepository.findById(request.getId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Histórico não encontrado"));

		Apoiador apoiador = apoiadorRepository.findById(request.getId_apoiador())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Apoiador não encontrado"));
		Usuario usuario = usuarioRepository.findById(request.getId_usuario())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

		historico.setApoiador(apoiador);
		historico.setUsuario(usuario);
		historico.setCampoAlterado(request.getCampo_alterado());
		historico.setValorAnterior(request.getValor_anterior());
		historico.setValorNovo(request.getValor_novo());

		return MapperUtil.paraHistoricoResponse(historicoApoiadorRepository.save(historico));
	}

	@Transactional
	public void apagar(UUID id) {
		segurancaUtil.exigirAdmin();
		if (!historicoApoiadorRepository.existsById(id)) {
			throw new RecursoNaoEncontradoException("Histórico não encontrado");
		}
		historicoApoiadorRepository.deleteById(id);
	}
}

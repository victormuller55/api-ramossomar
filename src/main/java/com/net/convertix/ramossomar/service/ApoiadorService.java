package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.ApoiadorRequest;
import com.net.convertix.ramossomar.dto.request.ApoiadorUpdateRequest;
import com.net.convertix.ramossomar.dto.response.ApoiadorResponse;
import com.net.convertix.ramossomar.exception.AcessoNegadoException;
import com.net.convertix.ramossomar.exception.NegocioException;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Apoiador;
import com.net.convertix.ramossomar.model.HistoricoApoiador;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import com.net.convertix.ramossomar.model.enums.Perfil;
import com.net.convertix.ramossomar.repository.ApoiadorRepository;
import com.net.convertix.ramossomar.repository.HistoricoApoiadorRepository;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.security.UsuarioAutenticado;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApoiadorService {

	private final ApoiadorRepository apoiadorRepository;
	private final UsuarioRepository usuarioRepository;
	private final HistoricoApoiadorRepository historicoApoiadorRepository;
	private final SegurancaUtil segurancaUtil;

	public ApoiadorService(
			ApoiadorRepository apoiadorRepository,
			UsuarioRepository usuarioRepository,
			HistoricoApoiadorRepository historicoApoiadorRepository,
			SegurancaUtil segurancaUtil
	) {
		this.apoiadorRepository = apoiadorRepository;
		this.usuarioRepository = usuarioRepository;
		this.historicoApoiadorRepository = historicoApoiadorRepository;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional
	public ApoiadorResponse criar(ApoiadorRequest request) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		validarAcessoLider(request.getId_lider(), autenticado);

		if (apoiadorRepository.existsByCpfAndDataExclusaoIsNull(request.getCpf())) {
			throw new NegocioException("Já existe um apoiador ativo com este CPF");
		}

		Usuario lider = buscarLider(request.getId_lider());

		Apoiador apoiador = new Apoiador();
		apoiador.setLider(lider);
		apoiador.setNome(request.getNome());
		apoiador.setCpf(request.getCpf());
		apoiador.setDataNascimento(request.getData_nascimento());
		apoiador.setTelefone(request.getTelefone());
		apoiador.setWhatsapp(request.getWhatsapp());
		apoiador.setCep(request.getCep());
		apoiador.setEndereco(request.getEndereco());
		apoiador.setNumero(request.getNumero());
		apoiador.setComplemento(request.getComplemento());
		apoiador.setBairro(request.getBairro());
		apoiador.setCidade(request.getCidade());
		apoiador.setLocalVotacao(request.getLocal_votacao());
		apoiador.setIntencaoVoto(request.getIntencao_voto());
		apoiador.setObservacoes(request.getObservacoes());

		return MapperUtil.paraApoiadorResponse(apoiadorRepository.save(apoiador));
	}

	@Transactional(readOnly = true)
	public List<ApoiadorResponse> listar(
			String nome,
			String cidade,
			UUID idLider,
			IntencaoVoto intencaoVoto,
			String cpf
	) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		UUID filtroLider = idLider;

		if (!autenticado.isAdmin()) {
			filtroLider = autenticado.getId();
		}

		return apoiadorRepository.filtrar(nome, cidade, filtroLider, intencaoVoto, cpf).stream()
				.map(MapperUtil::paraApoiadorResponse)
				.toList();
	}

	@Transactional
	public ApoiadorResponse alterar(ApoiadorUpdateRequest request) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		Apoiador apoiador = buscarAtivoPorId(request.getId());
		validarAcessoLider(apoiador.getLider().getId(), autenticado);
		validarAcessoLider(request.getId_lider(), autenticado);

		if (apoiadorRepository.existsByCpfAndDataExclusaoIsNullAndIdNot(request.getCpf(), request.getId())) {
			throw new NegocioException("Já existe um apoiador ativo com este CPF");
		}

		Usuario lider = buscarLider(request.getId_lider());
		Usuario usuarioAlteracao = usuarioRepository.findById(autenticado.getId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));

		registrarAlteracao(apoiador, usuarioAlteracao, "id_lider", apoiador.getLider().getId(), lider.getId());
		registrarAlteracao(apoiador, usuarioAlteracao, "nome", apoiador.getNome(), request.getNome());
		registrarAlteracao(apoiador, usuarioAlteracao, "cpf", apoiador.getCpf(), request.getCpf());
		registrarAlteracao(apoiador, usuarioAlteracao, "data_nascimento", apoiador.getDataNascimento(), request.getData_nascimento());
		registrarAlteracao(apoiador, usuarioAlteracao, "telefone", apoiador.getTelefone(), request.getTelefone());
		registrarAlteracao(apoiador, usuarioAlteracao, "whatsapp", apoiador.getWhatsapp(), request.getWhatsapp());
		registrarAlteracao(apoiador, usuarioAlteracao, "cep", apoiador.getCep(), request.getCep());
		registrarAlteracao(apoiador, usuarioAlteracao, "endereco", apoiador.getEndereco(), request.getEndereco());
		registrarAlteracao(apoiador, usuarioAlteracao, "numero", apoiador.getNumero(), request.getNumero());
		registrarAlteracao(apoiador, usuarioAlteracao, "complemento", apoiador.getComplemento(), request.getComplemento());
		registrarAlteracao(apoiador, usuarioAlteracao, "bairro", apoiador.getBairro(), request.getBairro());
		registrarAlteracao(apoiador, usuarioAlteracao, "cidade", apoiador.getCidade(), request.getCidade());
		registrarAlteracao(apoiador, usuarioAlteracao, "local_votacao", apoiador.getLocalVotacao(), request.getLocal_votacao());
		registrarAlteracao(apoiador, usuarioAlteracao, "intencao_voto", apoiador.getIntencaoVoto(), request.getIntencao_voto());
		registrarAlteracao(apoiador, usuarioAlteracao, "observacoes", apoiador.getObservacoes(), request.getObservacoes());

		apoiador.setLider(lider);
		apoiador.setNome(request.getNome());
		apoiador.setCpf(request.getCpf());
		apoiador.setDataNascimento(request.getData_nascimento());
		apoiador.setTelefone(request.getTelefone());
		apoiador.setWhatsapp(request.getWhatsapp());
		apoiador.setCep(request.getCep());
		apoiador.setEndereco(request.getEndereco());
		apoiador.setNumero(request.getNumero());
		apoiador.setComplemento(request.getComplemento());
		apoiador.setBairro(request.getBairro());
		apoiador.setCidade(request.getCidade());
		apoiador.setLocalVotacao(request.getLocal_votacao());
		apoiador.setIntencaoVoto(request.getIntencao_voto());
		apoiador.setObservacoes(request.getObservacoes());

		return MapperUtil.paraApoiadorResponse(apoiadorRepository.save(apoiador));
	}

	@Transactional
	public void apagar(UUID id) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		Apoiador apoiador = buscarAtivoPorId(id);
		validarAcessoLider(apoiador.getLider().getId(), autenticado);

		Usuario usuarioAlteracao = usuarioRepository.findById(autenticado.getId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));

		registrarAlteracao(apoiador, usuarioAlteracao, "data_exclusao", null, LocalDateTime.now());
		apoiador.setDataExclusao(LocalDateTime.now());
		apoiadorRepository.save(apoiador);
	}

	private void registrarAlteracao(
			Apoiador apoiador,
			Usuario usuario,
			String campo,
			Object valorAnterior,
			Object valorNovo
	) {
		if (Objects.equals(MapperUtil.valorComoTexto(valorAnterior), MapperUtil.valorComoTexto(valorNovo))) {
			return;
		}

		HistoricoApoiador historico = new HistoricoApoiador();
		historico.setApoiador(apoiador);
		historico.setUsuario(usuario);
		historico.setCampoAlterado(campo);
		historico.setValorAnterior(MapperUtil.valorComoTexto(valorAnterior));
		historico.setValorNovo(MapperUtil.valorComoTexto(valorNovo));
		historicoApoiadorRepository.save(historico);
	}

	private void validarAcessoLider(UUID idLider, UsuarioAutenticado autenticado) {
		if (autenticado.isAdmin()) {
			return;
		}
		if (autenticado.getPerfil() == Perfil.LIDER && autenticado.getId().equals(idLider)) {
			return;
		}
		throw new AcessoNegadoException("Você só pode gerenciar apoiadores do seu próprio cadastro de líder");
	}

	private Usuario buscarLider(UUID idLider) {
		Usuario lider = usuarioRepository.findById(idLider)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Líder não encontrado"));
		if (lider.getPerfil() != Perfil.LIDER && lider.getPerfil() != Perfil.ADMIN) {
			throw new NegocioException("O usuário informado não pode ser líder de apoiadores");
		}
		return lider;
	}

	private Apoiador buscarAtivoPorId(UUID id) {
		return apoiadorRepository.buscarAtivoPorId(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Apoiador não encontrado"));
	}
}

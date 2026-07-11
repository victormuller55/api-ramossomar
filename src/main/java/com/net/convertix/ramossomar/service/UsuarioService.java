package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.dto.request.UsuarioRequest;
import com.net.convertix.ramossomar.dto.request.UsuarioUpdateRequest;
import com.net.convertix.ramossomar.dto.response.UsuarioResponse;
import com.net.convertix.ramossomar.exception.NegocioException;
import com.net.convertix.ramossomar.exception.RecursoNaoEncontradoException;
import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import com.net.convertix.ramossomar.repository.UsuarioRepository;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import com.net.convertix.ramossomar.util.MapperUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

	private static final String PASTA_USUARIOS = "usuarios";

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final SegurancaUtil segurancaUtil;
	private final ArquivoStorageService arquivoStorageService;

	public UsuarioService(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			SegurancaUtil segurancaUtil,
			ArquivoStorageService arquivoStorageService
	) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.segurancaUtil = segurancaUtil;
		this.arquivoStorageService = arquivoStorageService;
	}

	@Transactional
	public UsuarioResponse criar(UsuarioRequest request) {
		segurancaUtil.exigirAdmin();

		if (usuarioRepository.existsByEmail(request.getEmail())) {
			throw new NegocioException("Já existe um usuário com este e-mail");
		}

		Usuario usuario = new Usuario();
		usuario.setNome(request.getNome());
		usuario.setEmail(request.getEmail().toLowerCase().trim());
		usuario.setSenha(passwordEncoder.encode(request.getSenha()));
		usuario.setPerfil(request.getPerfil());
		usuario.setTelefone(request.getTelefone());
		usuario.setAtivo(request.getAtivo() == null || request.getAtivo());

		return MapperUtil.paraUsuarioResponse(usuarioRepository.save(usuario));
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> listar(String nome, String email, Perfil perfil, Boolean ativo) {
		segurancaUtil.exigirAdmin();
		return usuarioRepository.filtrar(nome, email, perfil, ativo).stream()
				.map(MapperUtil::paraUsuarioResponse)
				.toList();
	}

	@Transactional
	public UsuarioResponse alterar(UsuarioUpdateRequest request) {
		segurancaUtil.exigirAdminOuProprioUsuario(request.getId());

		Usuario usuario = buscarPorId(request.getId());

		if (usuarioRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())) {
			throw new NegocioException("Já existe um usuário com este e-mail");
		}

		if (!segurancaUtil.isAdmin() && request.getPerfil() != usuario.getPerfil()) {
			throw new NegocioException("Você não pode alterar o próprio perfil");
		}

		usuario.setNome(request.getNome());
		usuario.setEmail(request.getEmail().toLowerCase().trim());
		usuario.setPerfil(request.getPerfil());
		usuario.setTelefone(request.getTelefone());
		usuario.setAtivo(request.getAtivo());

		if (request.getSenha() != null && !request.getSenha().isBlank()) {
			usuario.setSenha(passwordEncoder.encode(request.getSenha()));
		}

		return MapperUtil.paraUsuarioResponse(usuarioRepository.save(usuario));
	}

	@Transactional
	public UsuarioResponse uploadImagemPerfil(UUID id, MultipartFile imagem) {
		segurancaUtil.exigirAdminOuProprioUsuario(id);
		Usuario usuario = buscarPorId(id);

		String imagemAnterior = usuario.getImagem();
		String caminho = arquivoStorageService.salvarImagem(imagem, PASTA_USUARIOS);
		usuario.setImagem(caminho);
		Usuario salvo = usuarioRepository.save(usuario);

		if (imagemAnterior != null && !imagemAnterior.equals(caminho)) {
			arquivoStorageService.excluirSeExistir(imagemAnterior);
		}

		return MapperUtil.paraUsuarioResponse(salvo);
	}

	@Transactional
	public void apagar(UUID id) {
		segurancaUtil.exigirAdmin();
		Usuario usuario = buscarPorId(id);
		usuario.setAtivo(false);
		usuarioRepository.save(usuario);
	}

	public Usuario buscarPorId(UUID id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
	}
}

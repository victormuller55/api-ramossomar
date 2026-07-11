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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PublicacaoService {

	private static final String PASTA_PUBLICACOES = "publicacoes";
	private static final int MAX_IMAGENS = 3;

	private final PublicacaoRepository publicacaoRepository;
	private final UsuarioRepository usuarioRepository;
	private final SegurancaUtil segurancaUtil;
	private final ArquivoStorageService arquivoStorageService;

	public PublicacaoService(
			PublicacaoRepository publicacaoRepository,
			UsuarioRepository usuarioRepository,
			SegurancaUtil segurancaUtil,
			ArquivoStorageService arquivoStorageService
	) {
		this.publicacaoRepository = publicacaoRepository;
		this.usuarioRepository = usuarioRepository;
		this.segurancaUtil = segurancaUtil;
		this.arquivoStorageService = arquivoStorageService;
	}

	@Transactional
	public PublicacaoResponse criar(PublicacaoRequest request) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		validarAutor(request.getId_autor(), autenticado);

		Usuario autor = buscarAutor(request.getId_autor());

		Publicacao publicacao = new Publicacao();
		publicacao.setAutor(autor);
		publicacao.setTitulo(request.getTitulo());
		publicacao.setConteudo(request.getConteudo());

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

		Usuario autor = buscarAutor(request.getId_autor());
		publicacao.setAutor(autor);
		publicacao.setTitulo(request.getTitulo());
		publicacao.setConteudo(request.getConteudo());

		return MapperUtil.paraPublicacaoResponse(publicacaoRepository.save(publicacao));
	}

	@Transactional
	public PublicacaoResponse uploadImagens(UUID id, List<MultipartFile> imagens) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		Publicacao publicacao = buscarPorId(id);
		validarAutor(publicacao.getAutor().getId(), autenticado);

		List<MultipartFile> arquivosValidos = filtrarArquivos(imagens);
		if (arquivosValidos.isEmpty()) {
			throw new NegocioException("Envie de 1 a 3 imagens");
		}
		if (arquivosValidos.size() > MAX_IMAGENS) {
			throw new NegocioException("A publicação pode ter no máximo 3 imagens");
		}

		List<String> imagensAnteriores = publicacao.obterImagens();
		List<String> novosCaminhos = new ArrayList<>();
		for (MultipartFile arquivo : arquivosValidos) {
			novosCaminhos.add(arquivoStorageService.salvarImagem(arquivo, PASTA_PUBLICACOES));
		}

		publicacao.definirImagens(novosCaminhos);
		Publicacao salva = publicacaoRepository.save(publicacao);

		for (String anterior : imagensAnteriores) {
			if (!novosCaminhos.contains(anterior)) {
				arquivoStorageService.excluirSeExistir(anterior);
			}
		}

		return MapperUtil.paraPublicacaoResponse(salva);
	}

	@Transactional
	public void apagar(UUID id) {
		UsuarioAutenticado autenticado = segurancaUtil.obterUsuarioAutenticado();
		Publicacao publicacao = buscarPorId(id);
		validarAutor(publicacao.getAutor().getId(), autenticado);

		List<String> imagens = publicacao.obterImagens();
		publicacaoRepository.delete(publicacao);

		for (String imagem : imagens) {
			arquivoStorageService.excluirSeExistir(imagem);
		}
	}

	private List<MultipartFile> filtrarArquivos(List<MultipartFile> imagens) {
		if (imagens == null) {
			return List.of();
		}
		return imagens.stream()
				.filter(arquivo -> arquivo != null && !arquivo.isEmpty())
				.toList();
	}

	private void validarAutor(UUID idAutor, UsuarioAutenticado autenticado) {
		if (autenticado.isAdmin()) {
			return;
		}
		if (!autenticado.getId().equals(idAutor)) {
			throw new AcessoNegadoException("Você só pode gerenciar suas próprias publicações");
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

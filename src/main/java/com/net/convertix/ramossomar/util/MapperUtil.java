package com.net.convertix.ramossomar.util;

import com.net.convertix.ramossomar.dto.response.ApoiadorResponse;
import com.net.convertix.ramossomar.dto.response.HistoricoApoiadorResponse;
import com.net.convertix.ramossomar.dto.response.PublicacaoResponse;
import com.net.convertix.ramossomar.dto.response.TokenRefreshResponse;
import com.net.convertix.ramossomar.dto.response.UsuarioResponse;
import com.net.convertix.ramossomar.model.Apoiador;
import com.net.convertix.ramossomar.model.HistoricoApoiador;
import com.net.convertix.ramossomar.model.Publicacao;
import com.net.convertix.ramossomar.model.TokenRefresh;
import com.net.convertix.ramossomar.model.Usuario;

public final class MapperUtil {

	private MapperUtil() {
	}

	public static UsuarioResponse paraUsuarioResponse(Usuario usuario) {
		UsuarioResponse response = new UsuarioResponse();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setPerfil(usuario.getPerfil());
		response.setTelefone(usuario.getTelefone());
		response.setImagem(usuario.getImagem());
		response.setAtivo(usuario.getAtivo());
		response.setData_ultimo_login(usuario.getDataUltimoLogin());
		response.setData_criacao(usuario.getDataCriacao());
		response.setData_atualizacao(usuario.getDataAtualizacao());
		return response;
	}

	public static ApoiadorResponse paraApoiadorResponse(Apoiador apoiador) {
		ApoiadorResponse response = new ApoiadorResponse();
		response.setId(apoiador.getId());
		response.setId_lider(apoiador.getLider().getId());
		response.setNome_lider(apoiador.getLider().getNome());
		response.setNome(apoiador.getNome());
		response.setCpf(apoiador.getCpf());
		response.setData_nascimento(apoiador.getDataNascimento());
		response.setTelefone(apoiador.getTelefone());
		response.setWhatsapp(apoiador.getWhatsapp());
		response.setCep(apoiador.getCep());
		response.setEndereco(apoiador.getEndereco());
		response.setNumero(apoiador.getNumero());
		response.setComplemento(apoiador.getComplemento());
		response.setBairro(apoiador.getBairro());
		response.setCidade(apoiador.getCidade());
		response.setLocal_votacao(apoiador.getLocalVotacao());
		response.setIntencao_voto(apoiador.getIntencaoVoto());
		response.setObservacoes(apoiador.getObservacoes());
		response.setData_criacao(apoiador.getDataCriacao());
		response.setData_atualizacao(apoiador.getDataAtualizacao());
		response.setData_exclusao(apoiador.getDataExclusao());
		return response;
	}

	public static HistoricoApoiadorResponse paraHistoricoResponse(HistoricoApoiador historico) {
		HistoricoApoiadorResponse response = new HistoricoApoiadorResponse();
		response.setId(historico.getId());
		response.setId_apoiador(historico.getApoiador().getId());
		response.setId_usuario(historico.getUsuario().getId());
		response.setNome_usuario(historico.getUsuario().getNome());
		response.setCampo_alterado(historico.getCampoAlterado());
		response.setValor_anterior(historico.getValorAnterior());
		response.setValor_novo(historico.getValorNovo());
		response.setData_alteracao(historico.getDataAlteracao());
		return response;
	}

	public static PublicacaoResponse paraPublicacaoResponse(Publicacao publicacao) {
		PublicacaoResponse response = new PublicacaoResponse();
		response.setId(publicacao.getId());
		response.setId_autor(publicacao.getAutor().getId());
		response.setNome_autor(publicacao.getAutor().getNome());
		response.setTitulo(publicacao.getTitulo());
		response.setConteudo(publicacao.getConteudo());
		response.setMidia(publicacao.getMidia());
		response.setTipo_midia(publicacao.getTipoMidia());
		response.setData_criacao(publicacao.getDataCriacao());
		response.setData_atualizacao(publicacao.getDataAtualizacao());
		return response;
	}

	public static TokenRefreshResponse paraTokenRefreshResponse(TokenRefresh token) {
		TokenRefreshResponse response = new TokenRefreshResponse();
		response.setId(token.getId());
		response.setId_usuario(token.getUsuario().getId());
		response.setToken(token.getToken());
		response.setExpira_em(token.getExpiraEm());
		response.setData_criacao(token.getDataCriacao());
		return response;
	}

	public static String valorComoTexto(Object valor) {
		return valor == null ? null : String.valueOf(valor);
	}
}

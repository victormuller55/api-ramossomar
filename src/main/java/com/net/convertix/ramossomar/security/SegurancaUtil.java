package com.net.convertix.ramossomar.security;

import com.net.convertix.ramossomar.exception.AcessoNegadoException;
import com.net.convertix.ramossomar.model.enums.Perfil;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SegurancaUtil {

	public UsuarioAutenticado obterUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioAutenticado usuario)) {
			throw new AcessoNegadoException("Usuário não autenticado");
		}
		return usuario;
	}

	public UUID obterIdUsuarioAutenticado() {
		return obterUsuarioAutenticado().getId();
	}

	public boolean isAdmin() {
		return obterUsuarioAutenticado().isAdmin();
	}

	public void exigirAdmin() {
		if (!isAdmin()) {
			throw new AcessoNegadoException("Apenas administradores podem acessar este recurso");
		}
	}

	public void exigirAdminOuProprioUsuario(UUID idUsuario) {
		UsuarioAutenticado autenticado = obterUsuarioAutenticado();
		if (!autenticado.isAdmin() && !autenticado.getId().equals(idUsuario)) {
			throw new AcessoNegadoException("Você não tem permissão para acessar este recurso");
		}
	}

	public void exigirAdminOuLiderDono(UUID idLider) {
		UsuarioAutenticado autenticado = obterUsuarioAutenticado();
		if (autenticado.isAdmin()) {
			return;
		}
		if (autenticado.getPerfil() == Perfil.LIDER && autenticado.getId().equals(idLider)) {
			return;
		}
		throw new AcessoNegadoException("Você não tem permissão para acessar este recurso");
	}
}

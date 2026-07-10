package com.net.convertix.ramossomar.security;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioAutenticado implements UserDetails {

	private final UUID id;
	private final String email;
	private final String senha;
	private final String nome;
	private final Perfil perfil;
	private final boolean ativo;

	public UsuarioAutenticado(Usuario usuario) {
		this.id = usuario.getId();
		this.email = usuario.getEmail();
		this.senha = usuario.getSenha();
		this.nome = usuario.getNome();
		this.perfil = usuario.getPerfil();
		this.ativo = Boolean.TRUE.equals(usuario.getAtivo());
	}

	public UUID getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public boolean isAdmin() {
		return Perfil.ADMIN.equals(perfil);
	}

	public boolean isLider() {
		return Perfil.LIDER.equals(perfil);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return ativo;
	}
}

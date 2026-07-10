package com.net.convertix.ramossomar.util;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	private final SecretKey chaveSecreta;
	private final String issuer;
	private final ZoneId zona = ZoneId.of("America/Sao_Paulo");

	public JwtUtil(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.issuer}") String issuer
	) {
		this.chaveSecreta = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.issuer = issuer;
	}

	public String gerarToken(Usuario usuario) {
		LocalDateTime expiraEm = obterExpiracaoFimDoDia();
		Date agora = new Date();
		Date expiracao = Date.from(expiraEm.atZone(zona).toInstant());

		return Jwts.builder()
				.issuer(issuer)
				.subject(usuario.getId().toString())
				.claim("email", usuario.getEmail())
				.claim("nome", usuario.getNome())
				.claim("perfil", usuario.getPerfil().name())
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(chaveSecreta)
				.compact();
	}

	public LocalDateTime obterExpiracaoFimDoDia() {
		return LocalDate.now(zona).atTime(LocalTime.MAX).withNano(0);
	}

	public boolean tokenValido(String token) {
		try {
			extrairClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public UUID extrairIdUsuario(String token) {
		return UUID.fromString(extrairClaims(token).getSubject());
	}

	public String extrairEmail(String token) {
		return extrairClaims(token).get("email", String.class);
	}

	public Perfil extrairPerfil(String token) {
		return Perfil.valueOf(extrairClaims(token).get("perfil", String.class));
	}

	public LocalDateTime extrairExpiracao(String token) {
		Date expiration = extrairClaims(token).getExpiration();
		return LocalDateTime.ofInstant(expiration.toInstant(), zona);
	}

	private Claims extrairClaims(String token) {
		return Jwts.parser()
				.verifyWith(chaveSecreta)
				.requireIssuer(issuer)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}

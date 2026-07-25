package com.net.convertix.ramossomar.util;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtil {

	private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

	private final SecretKey chaveSecreta;
	private final String issuer;
	private final String audience;
	private final Duration expiration;
	private final Duration clockSkew;

	public JwtUtil(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.issuer}") String issuer,
			@Value("${jwt.audience}") String audience,
			@Value("${jwt.expiration-hours:24}") long expirationHours,
			@Value("${jwt.clock-skew-seconds:30}") long clockSkewSeconds
	) {
		if (!StringUtils.hasText(secret) || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("jwt.secret deve ter no mínimo 32 bytes");
		}
		this.chaveSecreta = Keys.hmacShaKeyFor(gerarChave(secret));
		this.issuer = issuer;
		this.audience = audience;
		this.expiration = Duration.ofHours(expirationHours);
		this.clockSkew = Duration.ofSeconds(clockSkewSeconds);
	}

	public String gerarToken(Usuario usuario) {
		Instant agora = Instant.now();
		Instant expiracao = agora.plus(expiration);

		return Jwts.builder()
				.issuer(issuer)
				.audience().add(audience).and()
				.subject(usuario.getId().toString())
				.claim("email", usuario.getEmail())
				.claim("nome", usuario.getNome())
				.claim("tipo", usuario.getPerfil().name())
				.claim("perfil", usuario.getPerfil().name())
				.issuedAt(Date.from(agora))
				.expiration(Date.from(expiracao))
				.signWith(chaveSecreta)
				.compact();
	}

	public LocalDateTime obterExpiracao() {
		return LocalDateTime.ofInstant(Instant.now().plus(expiration), ZONA);
	}

	/** @deprecated use {@link #obterExpiracao()} — mantido para compatibilidade */
	@Deprecated
	public LocalDateTime obterExpiracaoFimDoDia() {
		return obterExpiracao();
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
		Claims claims = extrairClaims(token);
		String tipo = claims.get("tipo", String.class);
		if (tipo == null) {
			tipo = claims.get("perfil", String.class);
		}
		return Perfil.valueOf(tipo);
	}

	public LocalDateTime extrairExpiracao(String token) {
		Date expirationDate = extrairClaims(token).getExpiration();
		return LocalDateTime.ofInstant(expirationDate.toInstant(), ZONA);
	}

	private Claims extrairClaims(String token) {
		return Jwts.parser()
				.verifyWith(chaveSecreta)
				.requireIssuer(issuer)
				.requireAudience(audience)
				.clockSkewSeconds(clockSkew.toSeconds())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private byte[] gerarChave(String secret) {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length >= 32) {
			return keyBytes;
		}
		try {
			return MessageDigest.getInstance("SHA-256").digest(keyBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Erro ao gerar chave JWT", e);
		}
	}
}

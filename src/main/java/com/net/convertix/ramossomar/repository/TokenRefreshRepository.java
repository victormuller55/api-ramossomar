package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.TokenRefresh;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRefreshRepository extends JpaRepository<TokenRefresh, UUID> {

	Optional<TokenRefresh> findByToken(String token);

	@Query("""
			SELECT t FROM TokenRefresh t
			WHERE (:idUsuario IS NULL OR t.usuario.id = :idUsuario)
			AND (:token IS NULL OR t.token = :token)
			ORDER BY t.dataCriacao DESC
			""")
	List<TokenRefresh> filtrar(
			@Param("idUsuario") UUID idUsuario,
			@Param("token") String token
	);

	void deleteByUsuarioId(UUID usuarioId);
}

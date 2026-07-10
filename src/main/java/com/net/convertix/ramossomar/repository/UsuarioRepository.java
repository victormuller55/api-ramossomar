package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.Usuario;
import com.net.convertix.ramossomar.model.enums.Perfil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, UUID id);

	@Query("""
			SELECT u FROM Usuario u
			WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
			AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
			AND (:perfil IS NULL OR u.perfil = :perfil)
			AND (:ativo IS NULL OR u.ativo = :ativo)
			""")
	List<Usuario> filtrar(
			@Param("nome") String nome,
			@Param("email") String email,
			@Param("perfil") Perfil perfil,
			@Param("ativo") Boolean ativo
	);
}

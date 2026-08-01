package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.Publicacao;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {

	@Query("""
			SELECT p FROM Publicacao p
			WHERE (:titulo IS NULL OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
			AND (:idAutor IS NULL OR p.autor.id = :idAutor)
			ORDER BY p.dataCriacao DESC
			""")
	Page<Publicacao> filtrar(
			@Param("titulo") String titulo,
			@Param("idAutor") UUID idAutor,
			Pageable pageable
	);
}

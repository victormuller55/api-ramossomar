package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.Cidade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CidadeRepository extends JpaRepository<Cidade, UUID> {

	Optional<Cidade> findByCodigoIbge(String codigoIbge);

	boolean existsByCodigoIbge(String codigoIbge);

	@Query("""
			SELECT c FROM Cidade c
			WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
			AND (:uf IS NULL OR c.uf = :uf)
			ORDER BY c.nome
			""")
	List<Cidade> filtrar(@Param("nome") String nome, @Param("uf") String uf);

	List<Cidade> findByUfOrderByNomeAsc(String uf);
}

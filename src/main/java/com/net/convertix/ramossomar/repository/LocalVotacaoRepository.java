package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.LocalVotacao;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocalVotacaoRepository extends JpaRepository<LocalVotacao, UUID> {

	Optional<LocalVotacao> findByCodigoTse(String codigoTse);

	boolean existsByCodigoTse(String codigoTse);

	@Query("""
			SELECT l FROM LocalVotacao l
			JOIN FETCH l.cidade c
			WHERE (:nome IS NULL OR LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
			AND (:idCidade IS NULL OR c.id = :idCidade)
			AND (:codigoIbge IS NULL OR c.codigoIbge = :codigoIbge)
			AND (:zonaEleitoral IS NULL OR l.zonaEleitoral = :zonaEleitoral)
			AND (:ativo IS NULL OR l.ativo = :ativo)
			ORDER BY l.nome
			""")
	List<LocalVotacao> filtrar(
			@Param("nome") String nome,
			@Param("idCidade") UUID idCidade,
			@Param("codigoIbge") String codigoIbge,
			@Param("zonaEleitoral") String zonaEleitoral,
			@Param("ativo") Boolean ativo
	);

	@Query("""
			SELECT l FROM LocalVotacao l
			JOIN FETCH l.cidade
			WHERE l.id = :id
			""")
	Optional<LocalVotacao> buscarPorId(@Param("id") UUID id);

	List<LocalVotacao> findByCodigoTseIn(List<String> codigosTse);
}

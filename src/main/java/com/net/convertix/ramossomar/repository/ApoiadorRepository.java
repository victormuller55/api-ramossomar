package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.Apoiador;
import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApoiadorRepository extends JpaRepository<Apoiador, UUID> {

	@Query("""
			SELECT a FROM Apoiador a
			WHERE a.dataExclusao IS NULL
			AND a.id = :id
			""")
	Optional<Apoiador> buscarAtivoPorId(@Param("id") UUID id);

	@Query("""
			SELECT a FROM Apoiador a
			WHERE a.dataExclusao IS NULL
			AND (:nome IS NULL OR LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
			AND (:cidade IS NULL OR LOWER(a.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
			AND (:idLider IS NULL OR a.lider.id = :idLider)
			AND (:intencaoVoto IS NULL OR a.intencaoVoto = :intencaoVoto)
			AND (:cpf IS NULL OR a.cpf = :cpf)
			""")
	List<Apoiador> filtrar(
			@Param("nome") String nome,
			@Param("cidade") String cidade,
			@Param("idLider") UUID idLider,
			@Param("intencaoVoto") IntencaoVoto intencaoVoto,
			@Param("cpf") String cpf
	);

	boolean existsByCpfAndDataExclusaoIsNull(String cpf);

	boolean existsByCpfAndDataExclusaoIsNullAndIdNot(String cpf, UUID id);
}

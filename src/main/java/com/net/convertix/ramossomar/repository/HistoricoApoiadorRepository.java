package com.net.convertix.ramossomar.repository;

import com.net.convertix.ramossomar.model.HistoricoApoiador;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoricoApoiadorRepository extends JpaRepository<HistoricoApoiador, UUID> {

	@Query("""
			SELECT h FROM HistoricoApoiador h
			WHERE (:idApoiador IS NULL OR h.apoiador.id = :idApoiador)
			AND (:idUsuario IS NULL OR h.usuario.id = :idUsuario)
			AND (:campoAlterado IS NULL OR LOWER(h.campoAlterado) LIKE LOWER(CONCAT('%', :campoAlterado, '%')))
			ORDER BY h.dataAlteracao DESC
			""")
	List<HistoricoApoiador> filtrar(
			@Param("idApoiador") UUID idApoiador,
			@Param("idUsuario") UUID idUsuario,
			@Param("campoAlterado") String campoAlterado
	);
}

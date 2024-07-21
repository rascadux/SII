package es.uma.informatica.sii.restunidades.repositorios;


import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadRepo extends JpaRepository<UnidadDocente, Long> {
    Optional<UnidadDocente> findByCursoAndGrupo(String curso, String grupo);
    Optional<UnidadDocente> findByCursoAndGrupoAndAula(String curso, String grupo, String aula);

}

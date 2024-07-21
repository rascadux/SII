package es.uma.informatica.sii.gruposalumnos.repositories;

import es.uma.informatica.sii.gruposalumnos.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoRepo extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findByNombre(String nombre);
}

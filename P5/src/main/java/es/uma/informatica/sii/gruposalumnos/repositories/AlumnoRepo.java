package es.uma.informatica.sii.gruposalumnos.repositories;

import es.uma.informatica.sii.gruposalumnos.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepo extends JpaRepository<Alumno, Long>{
    Optional<Alumno> findByDni(String dni);

}

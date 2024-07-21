package es.uma.informatica.sii.gruposalumnos.services;

import es.uma.informatica.sii.gruposalumnos.entities.Alumno;
import es.uma.informatica.sii.gruposalumnos.entities.Grupo;
import es.uma.informatica.sii.gruposalumnos.exceptions.ElementoNoExisteException;
import es.uma.informatica.sii.gruposalumnos.exceptions.ElementoYaExistenteException;
import es.uma.informatica.sii.gruposalumnos.exceptions.GrupoNoVacioException;
import es.uma.informatica.sii.gruposalumnos.repositories.AlumnoRepo;
import es.uma.informatica.sii.gruposalumnos.repositories.GrupoRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class LogicaGrupos {
    private GrupoRepo grupoRepo;
    private AlumnoRepo alumnoRepo;

    public LogicaGrupos(GrupoRepo grupoRepo, AlumnoRepo alumnoRepo) {
        this.grupoRepo = grupoRepo;
        this.alumnoRepo = alumnoRepo;
    }

    public List<Grupo> getGrupos() {
        return grupoRepo.findAll();
    }

    public Grupo addGrupo(Grupo grupo) {
        grupo.setId(null);
        grupo.setAlumnos(Collections.EMPTY_SET);
        grupoRepo.findByNombre(grupo.getNombre()).ifPresent(n -> {
            throw new ElementoYaExistenteException("Grupo ya existe");
        });
        return grupoRepo.save(grupo);
    }

    public Grupo getGrupo(Long id) {
        var grupo = grupoRepo.findById(id);
        if (grupo.isEmpty()) {
            throw new ElementoNoExisteException("Grupo no encontrado");
        } else {
            return grupo.get();
        }
    }

    public Grupo updateGrupo(Grupo grupo) {
        if (grupoRepo.existsById(grupo.getId())) {
            var opGrupo = grupoRepo.findByNombre(grupo.getNombre());
            if (opGrupo.isPresent() && opGrupo.get().getId() != grupo.getId()) {
                throw new ElementoYaExistenteException("Grupo ya existe");
            }
            opGrupo = grupoRepo.findById(grupo.getId());
            opGrupo.ifPresent(n -> {
                n.setNombre(grupo.getNombre());
            });
            return grupoRepo.save(opGrupo.get());
        } else {
            throw new ElementoNoExisteException("Grupo no encontrado");
        }
    }

    public void deleteGrupo(Long id) {
        var grupo = grupoRepo.findById(id);
        if (grupo.isPresent()) {
            grupoRepo.deleteById(id);
        } else {
            throw new ElementoNoExisteException("Grupo no encontrado");
        }
    }

    public Alumno addAlumno(Alumno alumno) {
        alumno.setId(null);
        alumno.setGrupos(Collections.EMPTY_SET);
        alumnoRepo.findByDni(alumno.getDni()).ifPresent(n -> {
            throw new ElementoYaExistenteException("Alumno ya existe");
        });

        return alumnoRepo.save(alumno);
    }

    public void deleteAlumno(Long idAlumno) {
        if (alumnoRepo.existsById(idAlumno)) {
            alumnoRepo.deleteById(idAlumno);
        } else {
            throw new ElementoNoExisteException("Alumno no existe");
        }
    }

    public Alumno updateAlumno(Alumno alumno) {
        if (alumnoRepo.existsById(alumno.getId())) {
            var opAlumno = alumnoRepo.findByDni(alumno.getDni());
            if (opAlumno.isPresent() && opAlumno.get().getId() != alumno.getId()) {
                throw new ElementoYaExistenteException("Alumno ya existe");
            }
            opAlumno = alumnoRepo.findById(alumno.getId());
            opAlumno.ifPresent(n -> {
                n.setDni(alumno.getDni());
            });
            return alumnoRepo.save(opAlumno.get());
        } else {
            throw new ElementoNoExisteException("Alumno no encontrado");
        }
    }

    public List<Alumno> getAlumnos() {
        return alumnoRepo.findAll();
    }

    public Alumno getAlumno(Long idAlumno) {
        var alumno = alumnoRepo.findById(idAlumno);
        if (alumno.isEmpty()) {
            throw new ElementoNoExisteException("Alumno no encontrado");
        }
        return alumno.get();
    }

    public void asociarGrupoAlumno(Long idGrupo, Long idAlumno){
        var alumno = getAlumno(idAlumno);
        var grupo = getGrupo(idGrupo);
        alumno.getGrupos().add(grupo);
    }

    public void desAsociarGrupoAlumno(Long idGrupo, Long idAlumno){
        var alumno = getAlumno(idAlumno);
        var grupo = getGrupo(idGrupo);
        alumno.getGrupos().remove(grupo);
    }
}

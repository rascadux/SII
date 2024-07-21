package es.uma.informatica.sii.gruposalumnos.controllers;

import es.uma.informatica.sii.gruposalumnos.dtos.AlumnoDTO;
import es.uma.informatica.sii.gruposalumnos.dtos.GrupoDTO;
import es.uma.informatica.sii.gruposalumnos.entities.Alumno;
import es.uma.informatica.sii.gruposalumnos.entities.Grupo;
import es.uma.informatica.sii.gruposalumnos.exceptions.ElementoNoExisteException;
import es.uma.informatica.sii.gruposalumnos.exceptions.ElementoYaExistenteException;
import es.uma.informatica.sii.gruposalumnos.exceptions.GrupoNoVacioException;
import es.uma.informatica.sii.gruposalumnos.services.LogicaGrupos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/alumnos")
public class AlumnosController {

    private LogicaGrupos logicaGrupos;

    public AlumnosController(LogicaGrupos logicaGrupos) {
        this.logicaGrupos = logicaGrupos;
    }

    @GetMapping
    public List<Alumno> getAlumnos() {
        return logicaGrupos.getAlumnos();
    }

    @PostMapping
    public ResponseEntity<Alumno> addAlumno(@RequestBody AlumnoDTO alumno, UriComponentsBuilder uriBuilder) {
        var alumnoEntity = Alumno.builder()
            .dni(alumno.getDni())
            .build();
        alumnoEntity = logicaGrupos.addAlumno(alumnoEntity);
        return ResponseEntity.created(uriBuilder
                .path("/alumnos/{id}")
                .buildAndExpand(alumnoEntity.getId())
                .toUri())
            .body(alumnoEntity);
    }

    @GetMapping("/{id}")
    public Alumno getAlumno(@PathVariable Long id) {
        return logicaGrupos.getAlumno(id);
    }


    @PutMapping("/{id}")
    public Alumno updateAlumno(@PathVariable Long id, @RequestBody AlumnoDTO alumno) {
        var alumnoEntity = Alumno.builder()
            .dni(alumno.getDni())
            .id(id)
            .build();
        return logicaGrupos.updateAlumno(alumnoEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAlumno(@PathVariable Long id) {
        logicaGrupos.deleteAlumno(id);
    }


    @ExceptionHandler(ElementoNoExisteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleElementoNoExisteException() {
    }

    @ExceptionHandler({ElementoYaExistenteException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleElementoYaExistenteException() {
    }







}

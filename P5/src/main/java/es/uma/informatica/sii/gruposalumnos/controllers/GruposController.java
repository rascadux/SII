package es.uma.informatica.sii.gruposalumnos.controllers;

import es.uma.informatica.sii.gruposalumnos.dtos.GrupoDTO;
import es.uma.informatica.sii.gruposalumnos.entities.Alumno;
import es.uma.informatica.sii.gruposalumnos.entities.Grupo;
import es.uma.informatica.sii.gruposalumnos.exceptions.ElementoNoExisteException;
import es.uma.informatica.sii.gruposalumnos.exceptions.ElementoYaExistenteException;
import es.uma.informatica.sii.gruposalumnos.services.LogicaGrupos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/grupos")
public class GruposController {

    private LogicaGrupos logicaGrupos;

    public GruposController(LogicaGrupos logicaGrupos) {
        this.logicaGrupos = logicaGrupos;
    }

    @GetMapping
    public List<Grupo> getGrupos() {
        return logicaGrupos.getGrupos();
    }

    @PostMapping()
    public ResponseEntity<Grupo> addGAlumno(@RequestBody GrupoDTO grupo, UriComponentsBuilder uriBuilder) {
        var grupoEntity = Grupo.builder().nombre(grupo.getNombre()).build();
        grupoEntity = logicaGrupos.addGrupo(grupoEntity);
        return ResponseEntity.created(uriBuilder
                .path("/grupos/{id}")
                .buildAndExpand(grupoEntity.getId())
                .toUri())
            .body(grupoEntity);
    }

    @GetMapping("/{id}")
    public Grupo getGrupo(@PathVariable Long id) {
        return logicaGrupos.getGrupo(id);
    }

    @PutMapping("/{id}")
    public Grupo updateGrupo(@PathVariable Long id, @RequestBody GrupoDTO grupo) {
        var grupoEntity = Grupo.builder().nombre(grupo.getNombre()).build();
        grupoEntity.setId(id);
        return logicaGrupos.updateGrupo(grupoEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGrupo(@PathVariable Long id) {
        logicaGrupos.deleteGrupo(id);
    }

    @PutMapping("/{idGrupo}/alumnos")
    public Set<Alumno> updateGrupo(@PathVariable Long idGrupo, @RequestParam List<Long> idAlumnos) {
        idAlumnos.stream()
            .forEach(idAlumno -> logicaGrupos.asociarGrupoAlumno(idGrupo, idAlumno));
        return logicaGrupos.getGrupo(idGrupo).getAlumnos();
    }

    @DeleteMapping("/{idGrupo}/alumnos")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGrupo(@PathVariable Long idGrupo, @RequestParam List<Long> idAlumnos) {
        idAlumnos.stream()
            .forEach(idAl -> logicaGrupos.desAsociarGrupoAlumno(idGrupo, idAl));
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

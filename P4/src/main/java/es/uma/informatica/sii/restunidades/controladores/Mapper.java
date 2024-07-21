package es.uma.informatica.sii.restunidades.controladores;

import es.uma.informatica.sii.restunidades.dtos.UnidadDTO;
import es.uma.informatica.sii.restunidades.dtos.UnidadNuevaDTO;
import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;

public class Mapper {
    public static UnidadDTO toUnidadDTO(UnidadDocente unidadDocente) {
        return UnidadDTO.builder()
            .id(unidadDocente.getId())
            .curso(unidadDocente.getCurso())
            .grupo(unidadDocente.getGrupo())
            .aula(unidadDocente.getAula())
            .build();
    }

    public static UnidadDocente toUnidad(UnidadNuevaDTO unidadNuevaDTO) {
        return UnidadDocente.builder()
            .curso(unidadNuevaDTO.getCurso())
            .grupo(unidadNuevaDTO.getGrupo())
            .aula(unidadNuevaDTO.getAula())
            .build();
    }
}

package es.uma.informatica.sii.gruposalumnos.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoDTO {
    private String nombre;

    public static GrupoDTO of(String nombre) {
        return GrupoDTO.builder().nombre(nombre).build();
    }
}

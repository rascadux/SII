package es.uma.informatica.sii.restunidades.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UnidadNuevaDTO {
    private String curso;
    private String grupo;
    private String aula;
}
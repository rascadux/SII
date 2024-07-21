package es.uma.informatica.sii.restunidades.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
public class UnidadDocente {
	
	@Id @GeneratedValue
	private Long id;
	private String curso;
	private String grupo;
	private String aula;
}

package es.uma.informatica.sii.gruposalumnos.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
public class Grupo {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String nombre;
    @ManyToMany(mappedBy = "grupos")
    @EqualsAndHashCode.Exclude
    private Set<Alumno> alumnos;
}

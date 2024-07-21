package es.uma.informatica.sii.restunidades.controladores;


import es.uma.informatica.sii.restunidades.dtos.UnidadDTO;
import es.uma.informatica.sii.restunidades.dtos.UnidadNuevaDTO;
import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import es.uma.informatica.sii.restunidades.excepciones.UnidadExistenteException;
import es.uma.informatica.sii.restunidades.excepciones.UnidadNoEncontrada;
import es.uma.informatica.sii.restunidades.repositorios.UnidadRepo;
import es.uma.informatica.sii.restunidades.servicios.LogicaUnidad;
import es.uma.informatica.sii.restunidades.servicios.LogicaUnidad;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/unidad")
public class ControladorRest {
	private LogicaUnidad servicio;

	public ControladorRest(LogicaUnidad servicioUnidad) {
		servicio = servicioUnidad;
	}

	@GetMapping
	public ResponseEntity<List<UnidadDTO>> listaDeUnidades() {
		return ResponseEntity.ok(servicio.getTodasUnidades().stream()
			.map(Mapper::toUnidadDTO)
			.toList());
	}

	@PostMapping
	public ResponseEntity<UnidadDTO> aniadirUnidad(@RequestBody UnidadNuevaDTO unidad, UriComponentsBuilder builder) throws UnidadExistenteException {

		// TODO

		try {
			UnidadDocente u = servicio.aniadirUnidad(Mapper.toUnidad(unidad));
			UnidadDTO uDTO = Mapper.toUnidadDTO(u);
			URI uri = builder
					.path("/api")
					.path("/v1")
					.path("/unidad")
					.path(String.format("/%d", uDTO.getId()))
					.build()
					.toUri();
			return ResponseEntity.created(uri).body(uDTO);
		} catch (UnidadExistenteException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	// TODO

	@GetMapping("/{id}")
	public ResponseEntity<UnidadDTO> getUnidad(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(Mapper.toUnidadDTO(servicio.getUnidad(id)));
		} catch (UnidadNoEncontrada e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<UnidadDTO> modificarUnidad(@PathVariable Long id, @RequestBody UnidadNuevaDTO unidad) {
		try {
			UnidadDocente u = servicio.modificarUnidad(id, Mapper.toUnidad(unidad));
			return ResponseEntity.ok(Mapper.toUnidadDTO(u));
		} catch (UnidadNoEncontrada e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (UnidadExistenteException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> borrarUnidad(@PathVariable Long id) {
		try {
			servicio.borrarUnidad(id);
			return ResponseEntity.ok().build();
		} catch (UnidadNoEncontrada e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}

package es.uma.informatica.sii.restunidades;

import es.uma.informatica.sii.restunidades.controladores.Mapper;
import es.uma.informatica.sii.restunidades.dtos.UnidadDTO;
import es.uma.informatica.sii.restunidades.dtos.UnidadNuevaDTO;
import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import es.uma.informatica.sii.restunidades.repositorios.UnidadRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de unidades")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UnidadDocenteApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value(value="${local.server.port}")
	private int port;
	
	@Autowired
	private UnidadRepo unidadRepository;
	
	@BeforeEach
	public void initializeDatabase() {
		unidadRepository.deleteAll();
	}
	
	private URI uri(String scheme, String host, int port, String ...paths) {
		UriBuilderFactory ubf = new DefaultUriBuilderFactory();
		UriBuilder ub = ubf.builder()
				.scheme(scheme)
				.host(host).port(port);
		for (String path: paths) {
			ub = ub.path(path);
		}
		return ub.build();
	}
	
	private RequestEntity<Void> get(String scheme, String host, int port, String path) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.get(uri)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}
	
	private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.delete(uri)
			.build();
		return peticion;
	}
	
	private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.post(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}
	
	private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.put(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}

	private void compruebaCampos(UnidadDTO expected, UnidadDTO actual) {
		assertThat(actual.getCurso()).isEqualTo(expected.getCurso());
		assertThat(actual.getGrupo()).isEqualTo(expected.getGrupo());
		assertThat(actual.getAula()).isEqualTo(expected.getAula());
	}

	private void compruebaCampos(UnidadNuevaDTO expected, UnidadDTO actual) {
		assertThat(actual.getCurso()).isEqualTo(expected.getCurso());
		assertThat(actual.getGrupo()).isEqualTo(expected.getGrupo());
		assertThat(actual.getAula()).isEqualTo(expected.getAula());
	}

	private void compruebaCampos(UnidadNuevaDTO expected, UnidadDocente actual) {
		assertThat(actual.getGrupo()).isEqualTo(expected.getGrupo());
		assertThat(actual.getCurso()).isEqualTo(expected.getCurso());
		assertThat(actual.getAula()).isEqualTo(expected.getAula());
	}
	
	
	@Nested
	@DisplayName("cuando no hay unidades")
	public class ListaVacia {
		
		@Test
		@DisplayName("devuelve la lista de unidades vacía")
		public void devuelveLista() {
			
			var peticion = get("http", "localhost",port, "/api/v1/unidad");
			
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<UnidadDTO>>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isEmpty();
		}
		
		@Nested
		@DisplayName("intenta insertar una unidad")
		public class InsertaUnidades {
			@Test
			@DisplayName("y se guarda con éxito")
			public void sinID() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("1")
					.grupo("A")
					.aula("2.0.5")
					.build();
				var peticion = post("http", "localhost", port, "/api/v1/unidad", unidad);
				
				var respuesta = restTemplate.exchange(peticion, Void.class);
				
				compruebaRespuesta(unidad, respuesta);
			}

			private void compruebaRespuesta(UnidadNuevaDTO unidad, ResponseEntity<Void> respuesta) {
				assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
				assertThat(respuesta.getHeaders().get("Location").get(0))
					.startsWith("http://localhost:"+port+"/api/v1/unidad");
				
				List<UnidadDocente> unidadDocentes = unidadRepository.findAll();
				assertThat(unidadDocentes).hasSize(1);
				assertThat(respuesta.getHeaders().get("Location").get(0))
					.endsWith("/"+ unidadDocentes.get(0).getId());
				compruebaCampos(unidad, unidadDocentes.get(0));
			}
		}
		
		@Test
		@DisplayName("devuelve error cuando se pide una unidad concreta")
		public void devuelveErrorAlConsultarUnidad() {
			var peticion = get("http", "localhost",port, "/api/v1/unidad/1");
			
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<UnidadDTO>>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isEqualTo(false);	
		}
		
		@Test
		@DisplayName("devuelve error cuando se modifica una unidad concreta")
		public void devuelveErrorAlModificarUnidad() {
			var unidad = UnidadNuevaDTO.builder()
				.curso("1")
				.grupo("A")
				.aula("2.0.5")
				.build();
			var peticion = put("http", "localhost",port, "/api/v1/unidad/1", unidad);
			
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
		}
		
		@Test
		@DisplayName("devuelve error cuando se elimina una unidad concreta")
		public void devuelveErrorAlEliminarUnidad() {
			var peticion = delete("http", "localhost",port, "/api/v1/unidad/1");
			
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
		}
	}
	
	@Nested
	@DisplayName("cuando hay unidades")
	public class ListaConDatos {
		private UnidadNuevaDTO grupo1A = UnidadNuevaDTO.builder()
			.curso("1")
			.grupo("A")
			.aula("2.0.5")
			.build();

		private UnidadNuevaDTO grupo2B = UnidadNuevaDTO.builder()
			.curso("2")
			.grupo("B")
			.aula("3.0.8b")
			.build();


		@BeforeEach
		public void introduceDatos() {
			unidadRepository.save(Mapper.toUnidad(grupo1A));
			unidadRepository.save(Mapper.toUnidad(grupo2B));
		}
		
		@Test
		@DisplayName("devuelve la lista de unidades correctamente")
		public void devuelveLista() {
			var peticion = get("http", "localhost",port, "/api/v1/unidad");
			
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<UnidadDTO>>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize(2);
		}
		
		@Nested
		@DisplayName("intenta insertar una unidad")
		public class InsertaUnidades {
			@Test
			@DisplayName("y lo consigue cuando no coincide el grupo con el de otra unidad")
			public void diferenteGrupo() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("3")
					.grupo("B")
					.aula("3.0.6")
					.build();
				var peticion = post("http", "localhost", port, "/api/v1/unidad", unidad);
				
				var respuesta = restTemplate.exchange(peticion, Void.class);
				
				compruebaRespuesta(unidad, respuesta);
			}

			@Test
			@DisplayName("pero da error cuando coincide el grupo con el de otra unidad")
			public void mismoGrupo() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("1")
					.grupo("A")
					.aula("3.0.1")
					.build();
				var peticion = post("http", "localhost", port, "/api/v1/unidad", unidad);

				var respuesta = restTemplate.exchange(peticion, Void.class);

				assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
				assertThat(respuesta.hasBody()).isFalse();
			}

			private void compruebaRespuesta(UnidadNuevaDTO unidad, ResponseEntity<Void> respuesta) {
				assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
				assertThat(respuesta.getHeaders().get("Location").get(0))
					.startsWith("http://localhost:"+port+"/api/v1/unidad");
				
				List<UnidadDocente> unidadDocentes = unidadRepository.findAll();
				assertThat(unidadDocentes).hasSize(3);
				
				UnidadDocente grupo3B = unidadDocentes.stream()
						.filter(c->c.getCurso().equals("3")
						 && c.getGrupo().equals("B"))
						.findAny()
						.get();
				
				assertThat(respuesta.getHeaders().get("Location").get(0))
					.endsWith("/"+grupo3B.getId());
				compruebaCampos(unidad, grupo3B);
			}
		}
		
		@Nested
		@DisplayName("al consultar una unidad concreta")
		public class ObtenerUnidades {
			@Test
			@DisplayName("lo devuelve cuando existe")
			public void devuelveUnidad() {
				var peticion = get("http", "localhost",port, "/api/v1/unidad/1");
				
				var respuesta = restTemplate.exchange(peticion, UnidadDTO.class);
				
				assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
				assertThat(respuesta.hasBody()).isTrue();
				assertThat(respuesta.getBody()).isNotNull();
			}
			
			@Test
			@DisplayName("da error cuando no existe")
			public void errorCuandoUnidadNoExiste() {
				var peticion = get("http", "localhost",port, "/api/v1/unidad/28");
				
				var respuesta = restTemplate.exchange(peticion,
						new ParameterizedTypeReference<List<UnidadDTO>>() {});
				
				assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
				assertThat(respuesta.hasBody()).isEqualTo(false);
			}
		}
		
		@Nested
		@DisplayName("al modificar una unidad")
		public class ModificarUnidades {
			@Test
			@DisplayName("lo modifica correctamente cuando existe")
			@DirtiesContext
			public void modificaCorrectamente() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("1")
					.grupo("A")
					.aula("4.1.1")
					.build();

				var peticion = put("http", "localhost",port, "/api/v1/unidad/1", unidad);
				
				var respuesta = restTemplate.exchange(peticion, UnidadDTO.class);
				
				assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
				UnidadDocente unidadDocenteBD = unidadRepository.findById(1L).get();
				compruebaCampos(unidad, unidadDocenteBD);
			}
			@Test
			@DisplayName("da error cuando no existe")
			public void errorCuandoNoExiste() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("1")
					.grupo("C")
					.aula("2.0.4")
					.build();
				var peticion = put("http", "localhost",port, "/api/v1/unidad/28", unidad);
				
				var respuesta = restTemplate.exchange(peticion,Void.class);
				
				assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
				assertThat(respuesta.hasBody()).isEqualTo(false);
			}

			@Test
			@DisplayName("da error cuando el grupo coincide con el de otra unidad")
			public void errorCuandoUnidadCoincideConOtra() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("2")
					.grupo("B")
					.aula("4.1.1")
					.build();
				var peticion = put("http", "localhost",port, "/api/v1/unidad/1", unidad);

				var respuesta = restTemplate.exchange(peticion,Void.class);

				assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
				assertThat(respuesta.hasBody()).isFalse();
			}

			@Test
			@DisplayName("no se modifica cuando el grupo coincide con el de otra unidad")
			public void noModificaCuandoElGrupoCoincideConOtro() {
				var unidad = UnidadNuevaDTO.builder()
					.curso("2")
					.grupo("B")
					.aula("4.1.1")
					.build();
				var peticion = put("http", "localhost",port, "/api/v1/unidad/1", unidad);
				restTemplate.exchange(peticion,Void.class);

				var previo = unidadRepository.findById(1L).get();
				compruebaCampos(grupo1A, previo);
			}
		}
		
		@Nested
		@DisplayName("al eliminar una unidad")
		public class EliminarUnidades {
			@Test
			@DisplayName("la elimina cuando existe")
			public void eliminaCorrectamente() {
				//List<Unidad> unidadesAntes = unidadRepository.findAll();
				//unidadesAntes.forEach(c->System.out.println(c));
				var peticion = delete("http", "localhost",port, "/api/v1/unidad/1");
				
				var respuesta = restTemplate.exchange(peticion,Void.class);
				
				assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
				List<UnidadDocente> unidadDocentes = unidadRepository.findAll();
				assertThat(unidadDocentes).hasSize(1);
				assertThat(unidadDocentes).allMatch(c->c.getId()!=1);
			}
			
			@Test
			@DisplayName("da error cuando no existe")
			public void errorCuandoNoExiste() {
				var peticion = delete("http", "localhost",port, "/api/v1/unidad/28");
				
				var respuesta = restTemplate.exchange(peticion,Void.class);
				
				assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
				assertThat(respuesta.hasBody()).isEqualTo(false);
			}
		}
	}
	
}

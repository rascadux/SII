package es.uma.informatica.sii.gruposalumnos;

import es.uma.informatica.sii.gruposalumnos.entities.Alumno;
import es.uma.informatica.sii.gruposalumnos.entities.Grupo;
import es.uma.informatica.sii.gruposalumnos.repositories.AlumnoRepo;
import es.uma.informatica.sii.gruposalumnos.repositories.GrupoRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de alumnos y alumnos")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GruposAlumnosApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private GrupoRepo grupoRepository;

    @Autowired
    private AlumnoRepo alumnoRepository;

    @BeforeEach
    public void initializeDatabase() {
        grupoRepository.deleteAll();
        alumnoRepository.deleteAll();
    }

    private URI uri(String scheme, String host, int port, String... paths) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub = ubf.builder()
            .scheme(scheme)
            .host(host).port(port);
        for (String path : paths) {
            ub = ub.path(path);
        }
        return ub.build();
    }

    private RequestEntity<Void> get(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.get(uri)
            .accept(MediaType.APPLICATION_JSON)
            .build();
        return peticion;
    }

    private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.delete(uri)
            .build();
        return peticion;
    }

    private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.post(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .body(object);
        return peticion;
    }

    private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.put(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .body(object);
        return peticion;
    }


    @Nested
    @DisplayName("cuando no hay grupos")
    public class SinGrupos {

        @Test
        @DisplayName("devuelve la lista de grupos vacía")
        public void devuelveGrupos() {
            var peticion = get("http", "localhost", port, "/grupos");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Set<Grupo>>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }

        @Test
        @DisplayName("añade un grupo")
        public void añadeGrupo() {
            var grupo = Grupo.builder()
                .nombre("Grupo 1")
                .build();
            var peticion = post("http", "localhost", port, "/grupos", grupo);

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Grupo>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Grupo 1");
        }

        @Test
        @DisplayName("no devuelve un grupo")
        public void devuelveGrupo() {
            var peticion = get("http", "localhost", port, "/grupos/1");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Grupo>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("no actualiza un grupo")
        public void actualizaGrupoNoEncontrado() {
            var grupo = Grupo.builder().nombre("Grupo 1").build();
            var peticion = put("http", "localhost", port, "/grupos/1", grupo);

            var respuesta = restTemplate.exchange(peticion, Grupo.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("no elimina un grupo")
        public void eliminaGrupo() {
            var peticion = delete("http", "localhost", port, "/grupos/1");

            var respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("no actualiza los alumnos de un grupo")
        public void actualizaAlumnosGrupo() {
            var peticion = put("http", "localhost", port, "/grupos/1/alumnos", List.of(1L));

            var respuesta = restTemplate.exchange(peticion, String.class);

            System.out.println("Respuesta: " + respuesta.getBody());

            assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
        }

        @Test
        @DisplayName("no elimina los alumnos de un grupo")
        public void eliminaAlumnosGrupo() {
            var peticion = delete("http", "localhost", port, "/grupos/1/alumnos");

            var respuesta =  restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("cuando hay grupos")
    public class ConGrupos {

        @BeforeEach
        public void init() {
            var grupo = Grupo.builder().nombre("Grupo 1").build();
            grupoRepository.save(grupo);
            var grupo2 = Grupo.builder().nombre("Grupo 2").build();
            grupoRepository.save(grupo2);
            var alumno = Alumno.builder()
                .dni("12345678A")
                .build();
            alumnoRepository.save(alumno);
            var peticion = put("http", "localhost", port, "/grupos/1/alumnos", List.of(1L));
            restTemplate.exchange(peticion, String.class);
        }

        @Test
        @DisplayName("devuelve la lista de grupos")
        public void devuelveGrupos() {
            var peticion = get("http", "localhost", port, "/grupos");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Set<Grupo>>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("añade un grupo")
        public void añadeGrupo() {
            var grupo = Grupo.builder()
                .nombre("Grupo 3")
                .build();
            var peticion = post("http", "localhost", port, "/grupos", grupo);

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Grupo>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Grupo 3");
        }


        @Test
        @DisplayName("devuelve un grupo")
        public void devuelveGrupo() {
            var peticion = get("http", "localhost", port, "/grupos/1");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Grupo>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Grupo 1");
        }

        @Test
        @DisplayName("actualiza un grupo")
        public void actualizaGrupo() {
            var grupo = Grupo.builder()
                .nombre("Grupo 3")
                .build();
            var peticion = put("http", "localhost", port, "/grupos/1", grupo);

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Grupo>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Grupo 3");
        }

        @Test
        @DisplayName("no actualiza grupo ya existente")
        public void actualizaGrupoExistente() {
            var grupo = Grupo.builder().nombre("Grupo 2").build();
            var peticion = put("http", "localhost", port, "/grupos/1", grupo);

            var respuesta = restTemplate.exchange(peticion, Grupo.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("elimina un grupo")
        public void eliminaGrupo() {
            var peticion = delete("http", "localhost", port, "/grupos/1");

            var respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(grupoRepository.findById(1L)).isEmpty();
        }

        @Test
        @DisplayName("actualiza los alumnos de un grupo")
        public void actualizaAlumnosGrupo() {
            var alumno = Alumno.builder()
                .dni("99999999B")
                .build();
            alumnoRepository.save(alumno);

            var peticion = put("http", "localhost", port, "/grupos/1/alumnos", List.of(alumno));

            var respuesta = restTemplate.exchange(peticion, String.class);



            assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
        }

        @Test
        @DisplayName("elimina los alumnos de un grupo")
        public void eliminaAlumnosGrupo() {
            var peticion = delete("http", "localhost", port, "/grupos/1/alumnos");

            var respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
        }


    }

    @Nested
    @DisplayName("cuando hay alumnos")
    public class ConAlumnos {

        @BeforeEach
        public void init() {
            var grupo = Grupo.builder()
                    .nombre("Grupo 1")
                    .build();
            grupoRepository.save(grupo);
            var alumno = Alumno.builder()
                    .dni("12345678A")
                    .build();
            alumnoRepository.save(alumno);
            var alumno2 = Alumno.builder().dni("22222222M").build();
            alumnoRepository.save(alumno2);
            put("http", "localhost", port, "/grupos/1/alumnos", List.of(2L));
        }

        @Test
        @DisplayName("devuelve la lista de alumnos")
        public void devuelveAlumnos() {
            var peticion = get("http", "localhost", port, "/alumnos");

            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<Set<Alumno>>() {
                    });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("añade un alumno")
        public void añadeAlumno() {
            var alumno = Alumno.builder()
                    .dni("99999999B")
                    .build();
            var peticion = post("http", "localhost", port, "/alumnos", alumno);

            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<Alumno>() {
                    });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getDni()).isEqualTo("99999999B");
        }

        @Test
        @DisplayName("no añade un alumno con el mismo dni")
        public void añadeAlumnoMismoDni() {
            var alumno = Alumno.builder()
                    .dni("12345678A")
                    .build();
            var peticion = post("http", "localhost", port, "/alumnos", alumno);

            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<Alumno>() {
                    });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("devuelve un alumno")
        public void devuelveAlumno() {
            var peticion = get("http", "localhost", port, "/alumnos/1");

            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<Alumno>() {
                    });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getDni()).isEqualTo("12345678A");
        }

        @Test
        @DisplayName("actualiza un alumno")
        public void actualizaAlumno() {
            var alumno = Alumno.builder()
                    .dni("99999999B")
                    .build();
            var peticion = put("http", "localhost", port, "/alumnos/1", alumno);

            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<Alumno>() {
                    });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getDni()).isEqualTo("99999999B");
        }

        @Test
        @DisplayName("dni asignado ya existente")
        public void actualizaAlumnoExistente() {
            var alumno = Alumno.builder().dni("22222222M").build();
            var peticion = put("http", "localhost", port, "/alumnos/1", alumno);

            var respuesta = restTemplate.exchange(peticion, Alumno.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("elimina un alumno")
        public void eliminaAlumno() {
            var peticion = delete("http", "localhost", port, "/alumnos/1");

            var respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(alumnoRepository.findById(1L)).isEmpty();
        }

    }

    @Nested
    @DisplayName("cuando no hay alumnos")
    public class SinAlumnos {

        @Test
        @DisplayName("devuelve la lista de alumnos vacía")
        public void devuelveAlumnos() {
            var peticion = get("http", "localhost", port, "/alumnos");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Set<Alumno>>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }

        @Test
        @DisplayName("añade un alumno")
        public void añadeAlumno() {
            var alumno = Alumno.builder()
                .dni("12345678A")
                .build();
            var peticion = post("http", "localhost", port, "/alumnos", alumno);

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Alumno>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody().getId()).isNotNull();
            assertThat(respuesta.getBody().getDni()).isEqualTo("12345678A");
        }

        @Test
        @DisplayName("no devuelve un alumno")
        public void devuelveAlumno() {
            var peticion = get("http", "localhost", port, "/alumnos/1");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Alumno>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }


        @Test
        @DisplayName("no actualiza un alumno no encontrado")
        public void actualizaAlumnoNoEncontrado() {
            var alumno = Alumno.builder().dni("11111111N").build();
            var peticion = put("http", "localhost", port, "/alumnos/1", alumno);

            var respuesta = restTemplate.exchange(peticion, Alumno.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("no elimina un alumno")
        public void eliminaAlumno() {
            var peticion = delete("http", "localhost", port, "/alumnos/1");

            var respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }
    }


}
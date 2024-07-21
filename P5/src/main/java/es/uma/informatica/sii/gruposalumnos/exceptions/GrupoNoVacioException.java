package es.uma.informatica.sii.gruposalumnos.exceptions;

public class GrupoNoVacioException extends RuntimeException {
    public GrupoNoVacioException(String message) {
        super(message);
    }

    public GrupoNoVacioException() {
        super();
    }
}

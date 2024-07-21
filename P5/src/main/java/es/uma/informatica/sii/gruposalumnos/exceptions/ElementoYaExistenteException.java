package es.uma.informatica.sii.gruposalumnos.exceptions;

public class ElementoYaExistenteException extends RuntimeException {
    public ElementoYaExistenteException(String message) {
        super(message);
    }

    public ElementoYaExistenteException() {
        super();
    }
}

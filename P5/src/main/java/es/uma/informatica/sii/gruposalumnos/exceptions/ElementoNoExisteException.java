package es.uma.informatica.sii.gruposalumnos.exceptions;

public class ElementoNoExisteException extends RuntimeException {
    public ElementoNoExisteException(String message) {
        super(message);
    }

    public ElementoNoExisteException() {
        super();
    }
}

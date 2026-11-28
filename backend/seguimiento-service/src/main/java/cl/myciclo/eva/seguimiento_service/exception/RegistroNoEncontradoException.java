package cl.myciclo.eva.seguimiento_service.exception;

public class RegistroNoEncontradoException extends RuntimeException {

    public RegistroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

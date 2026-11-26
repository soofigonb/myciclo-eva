package cl.myciclo.eva.usuarios.exception;

public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}

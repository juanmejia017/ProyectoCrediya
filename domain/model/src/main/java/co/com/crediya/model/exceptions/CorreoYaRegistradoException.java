package co.com.crediya.model.exceptions;

public class CorreoYaRegistradoException extends BusinessException {
    public CorreoYaRegistradoException(String correo) {
        super("CORREO_YA_REGISTRADO", "El correo " + correo + " ya está registrado por otro solicitante");
    }
}
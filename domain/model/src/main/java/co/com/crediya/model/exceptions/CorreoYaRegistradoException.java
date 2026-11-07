package co.com.crediya.model.exceptions;

public class CorreoYaRegistradoException extends BusinessException {
    public CorreoYaRegistradoException() {
        super("CORREO_YA_REGISTRADO", "El correo electrónico ya está registrado por otro solicitante");
    }
}

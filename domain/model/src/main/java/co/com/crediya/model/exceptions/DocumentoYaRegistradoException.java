package co.com.crediya.model.exceptions;

public class DocumentoYaRegistradoException extends BusinessException {
    public DocumentoYaRegistradoException() {
        super("DOCUMENTO_YA_REGISTRADO", "El documento de identidad ya está registrado por otro solicitante");
    }
}

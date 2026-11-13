package co.com.crediya.api.dto;

import co.com.crediya.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UsuarioResponse(
        Long idUsuario,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String correoElectronico,
        String documentoIdentidad,
        Long idRol,
        BigDecimal salarioBase
) {
    public static UsuarioResponse fromDomain(User user) {
        return new UsuarioResponse(
                user.getIdUsuario(),
                user.getNombres(),
                user.getApellidos(),
                user.getFechaNacimiento(),
                user.getDireccion(),
                user.getTelefono(),
                user.getCorreoElectronico(),
                user.getDocumentoIdentidad(),
                user.getIdRol(),
                user.getSalarioBase()
        );
    }
}
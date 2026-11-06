package co.com.crediya.api.dto;

import co.com.crediya.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarUsuarioRequest(
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
    public User toDomain() {
        return User.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .fechaNacimiento(fechaNacimiento)
                .direccion(direccion)
                .telefono(telefono)
                .correoElectronico(correoElectronico)
                .documentoIdentidad(documentoIdentidad)
                .idRol(idRol)
                .salarioBase(salarioBase)
                .build();
    }
}
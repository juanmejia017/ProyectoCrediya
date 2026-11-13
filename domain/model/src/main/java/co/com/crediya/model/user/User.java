package co.com.crediya.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private String documentoIdentidad;
    private Long idRol;
    private BigDecimal salarioBase;

    /**
     * Devuelve una copia lista para guardar: sin id, sin espacios sobrantes,
     * correo en minúsculas y campos opcionales vacíos convertidos en null.
     */
    public User normalizado() {
        return toBuilder()
                .idUsuario(null)
                .nombres(nombres.trim())
                .apellidos(apellidos.trim())
                .correoElectronico(correoElectronico.trim().toLowerCase(Locale.ROOT))
                .telefono(limpiar(telefono))
                .documentoIdentidad(limpiar(documentoIdentidad))
                .direccion(limpiar(direccion))
                .build();
    }

    private static String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

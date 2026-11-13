package co.com.crediya.model.user;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void normalizaEspaciosYCorreo() {
        User user = User.builder()
                .idUsuario(99L)
                .nombres("  Juan ")
                .apellidos(" Mejia ")
                .correoElectronico("  Juan@Correo.COM ")
                .telefono(" 3001234567 ")
                .documentoIdentidad(" 1020304050 ")
                .direccion(" Calle 10 ")
                .salarioBase(new BigDecimal("2500000"))
                .build();

        User normalizado = user.normalizado();

        assertNull(normalizado.getIdUsuario());
        assertEquals("Juan", normalizado.getNombres());
        assertEquals("Mejia", normalizado.getApellidos());
        assertEquals("juan@correo.com", normalizado.getCorreoElectronico());
        assertEquals("3001234567", normalizado.getTelefono());
        assertEquals("1020304050", normalizado.getDocumentoIdentidad());
        assertEquals("Calle 10", normalizado.getDireccion());
    }

    @Test
    void convierteOpcionalesVaciosEnNull() {
        User user = User.builder()
                .nombres("Juan")
                .apellidos("Mejia")
                .correoElectronico("juan@correo.com")
                .telefono(" ")
                .documentoIdentidad("")
                .direccion(null)
                .build();

        User normalizado = user.normalizado();

        assertNull(normalizado.getTelefono());
        assertNull(normalizado.getDocumentoIdentidad());
        assertNull(normalizado.getDireccion());
    }

    @Test
    void noModificaElUsuarioOriginal() {
        User user = User.builder().nombres(" Juan ").apellidos("Mejia").correoElectronico("A@B.co").build();

        user.normalizado();

        assertEquals(" Juan ", user.getNombres());
    }
}

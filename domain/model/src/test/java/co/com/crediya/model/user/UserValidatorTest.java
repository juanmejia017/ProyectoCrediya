package co.com.crediya.model.user;

import co.com.crediya.model.exceptions.DatosInvalidosException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {

    private User.UserBuilder usuarioValido() {
        return User.builder()
                .nombres("Juan")
                .apellidos("Mejia")
                .correoElectronico("juan@correo.com")
                .documentoIdentidad("1020304050")
                .telefono("3001234567")
                .fechaNacimiento(LocalDate.of(2000, 7, 12))
                .salarioBase(new BigDecimal("2500000"));
    }

    @Test
    void aceptaUsuarioValido() {
        assertDoesNotThrow(() -> UserValidator.validar(usuarioValido().build()));
    }

    @Test
    void rechazaCuerpoNulo() {
        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(null));
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        User user = User.builder().nombres(" ").build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(user));

        assertEquals(4, ex.getErrores().size());
    }

    @Test
    void rechazaCorreoConFormatoInvalido() {
        User user = usuarioValido().correoElectronico("correo-malo").build();

        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(user));
    }

    @Test
    void rechazaSalarioFueraDeRango() {
        User mayor = usuarioValido().salarioBase(new BigDecimal("15000001")).build();
        User negativo = usuarioValido().salarioBase(new BigDecimal("-1")).build();

        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(mayor));
        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(negativo));
    }

    @Test
    void aceptaSalarioEnLosLimites() {
        assertDoesNotThrow(() -> UserValidator.validar(usuarioValido().salarioBase(BigDecimal.ZERO).build()));
        assertDoesNotThrow(() -> UserValidator.validar(usuarioValido().salarioBase(new BigDecimal("15000000")).build()));
    }

    @Test
    void rechazaFechaNacimientoFutura() {
        User user = usuarioValido().fechaNacimiento(LocalDate.now().plusDays(1)).build();

        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(user));
    }

    @Test
    void rechazaTelefonoInvalido() {
        User user = usuarioValido().telefono("12ab").build();

        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(user));
    }

    @Test
    void rechazaDocumentoInvalido() {
        User user = usuarioValido().documentoIdentidad("12-3").build();

        assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(user));
    }

    @Test
    void permiteCamposOpcionalesVacios() {
        User user = usuarioValido().telefono(null).documentoIdentidad(" ").fechaNacimiento(null).build();

        assertDoesNotThrow(() -> UserValidator.validar(user));
    }

    @Test
    void acumulaTodosLosErroresDeFormato() {
        User user = usuarioValido()
                .correoElectronico("correo-malo")
                .salarioBase(new BigDecimal("20000000"))
                .fechaNacimiento(LocalDate.now().plusYears(1))
                .telefono("12ab")
                .documentoIdentidad("12")
                .build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> UserValidator.validar(user));

        assertEquals(5, ex.getErrores().size());
    }
}

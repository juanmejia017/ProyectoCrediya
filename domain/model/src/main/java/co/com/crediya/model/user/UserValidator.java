package co.com.crediya.model.user;

import co.com.crediya.model.exceptions.DatosInvalidosException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de formato del usuario. Pertenecen al modelo porque valen para cualquier
 * caso de uso que cree o modifique un usuario.
 */
public final class UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+?\\d{7,15}$");
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,20}$");
    private static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal("15000000");

    private UserValidator() {
    }

    public static void validar(User user) {
        if (user == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
        List<String> errores = new ArrayList<>();
        validarObligatorios(user, errores);
        validarOpcionales(user, errores);
        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
    }

    private static void validarObligatorios(User user, List<String> errores) {
        if (estaVacio(user.getNombres())) {
            errores.add("El campo nombres es obligatorio");
        }
        if (estaVacio(user.getApellidos())) {
            errores.add("El campo apellidos es obligatorio");
        }
        if (estaVacio(user.getCorreoElectronico())) {
            errores.add("El campo correoElectronico es obligatorio");
        } else if (!EMAIL_PATTERN.matcher(user.getCorreoElectronico().trim()).matches()) {
            errores.add("El campo correoElectronico no tiene un formato válido");
        }
        if (user.getSalarioBase() == null) {
            errores.add("El campo salarioBase es obligatorio");
        } else if (user.getSalarioBase().compareTo(SALARIO_MINIMO) < 0
                || user.getSalarioBase().compareTo(SALARIO_MAXIMO) > 0) {
            errores.add("El campo salarioBase debe estar entre 0 y 15000000");
        }
    }

    private static void validarOpcionales(User user, List<String> errores) {
        if (user.getFechaNacimiento() != null && !user.getFechaNacimiento().isBefore(LocalDate.now())) {
            errores.add("El campo fechaNacimiento debe ser una fecha pasada");
        }
        if (!estaVacio(user.getTelefono()) && !TELEFONO_PATTERN.matcher(user.getTelefono().trim()).matches()) {
            errores.add("El campo telefono debe tener entre 7 y 15 dígitos (puede iniciar con +)");
        }
        if (!estaVacio(user.getDocumentoIdentidad())
                && !DOCUMENTO_PATTERN.matcher(user.getDocumentoIdentidad().trim()).matches()) {
            errores.add("El campo documentoIdentidad debe tener entre 5 y 20 caracteres alfanuméricos");
        }
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}

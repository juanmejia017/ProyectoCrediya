package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal("15000000");

    private final UserRepository userRepository;

    public User registrar(User user) {
        validar(user);

        String correo = user.getCorreoElectronico().trim().toLowerCase();
        if (userRepository.existsByCorreoElectronico(correo)) {
            throw new CorreoYaRegistradoException(correo);
        }

        User normalizado = user.toBuilder()
                .idUsuario(null)
                .nombres(user.getNombres().trim())
                .apellidos(user.getApellidos().trim())
                .correoElectronico(correo)
                .build();

        return userRepository.save(normalizado);
    }

    private void validar(User user) {
        List<String> errores = new ArrayList<>();

        if (user == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
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

        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
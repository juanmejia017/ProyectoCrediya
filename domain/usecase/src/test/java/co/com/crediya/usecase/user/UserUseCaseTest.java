package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUseCaseTest {

    private FakeUserRepository repository;
    private UserUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new FakeUserRepository();
        useCase = new UserUseCase(repository);
    }

    private User.UserBuilder usuarioValido() {
        return User.builder()
                .nombres("Juan")
                .apellidos("Mejia")
                .correoElectronico("Juan@Correo.com")
                .salarioBase(new BigDecimal("2500000"));
    }

    @Test
    void registraUsuarioValidoYNormalizaCorreo() {
        User creado = useCase.registrar(usuarioValido().build());

        assertNotNull(creado.getIdUsuario());
        assertEquals("juan@correo.com", creado.getCorreoElectronico());
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        User user = User.builder().nombres(" ").build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> useCase.registrar(user));

        assertEquals(4, ex.getErrores().size());
    }

    @Test
    void rechazaCorreoConFormatoInvalido() {
        User user = usuarioValido().correoElectronico("correo-malo").build();

        assertThrows(DatosInvalidosException.class, () -> useCase.registrar(user));
    }

    @Test
    void rechazaSalarioFueraDeRango() {
        User mayor = usuarioValido().salarioBase(new BigDecimal("15000001")).build();
        User negativo = usuarioValido().salarioBase(new BigDecimal("-1")).build();

        assertThrows(DatosInvalidosException.class, () -> useCase.registrar(mayor));
        assertThrows(DatosInvalidosException.class, () -> useCase.registrar(negativo));
    }

    @Test
    void aceptaSalarioEnLosLimites() {
        useCase.registrar(usuarioValido().salarioBase(BigDecimal.ZERO).build());
        useCase.registrar(usuarioValido().correoElectronico("otro@correo.com")
                .salarioBase(new BigDecimal("15000000")).build());

        assertEquals(2, repository.guardados.size());
    }

    @Test
    void rechazaCorreoYaRegistrado() {
        useCase.registrar(usuarioValido().build());

        assertThrows(CorreoYaRegistradoException.class, () -> useCase.registrar(usuarioValido().build()));
        assertTrue(repository.guardados.size() == 1);
    }

    private static class FakeUserRepository implements UserRepository {
        private final List<User> guardados = new ArrayList<>();

        @Override
        public User save(User user) {
            User guardado = user.toBuilder().idUsuario((long) guardados.size() + 1).build();
            guardados.add(guardado);
            return guardado;
        }

        @Override
        public boolean existsByCorreoElectronico(String correoElectronico) {
            return guardados.stream().anyMatch(u -> u.getCorreoElectronico().equals(correoElectronico));
        }
    }
}

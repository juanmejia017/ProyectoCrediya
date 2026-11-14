package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .nombres(" Juan ")
                .apellidos("Mejia")
                .correoElectronico("Juan@Correo.com")
                .documentoIdentidad("1020304050")
                .telefono("3001234567")
                .fechaNacimiento(LocalDate.of(2000, 7, 12))
                .salarioBase(new BigDecimal("2500000"));
    }

    @Test
    void registraUsuarioValidoYNormalizaDatos() {
        User creado = useCase.registrar(usuarioValido().build());

        assertNotNull(creado.getIdUsuario());
        assertEquals("juan@correo.com", creado.getCorreoElectronico());
        assertEquals("Juan", creado.getNombres());
    }

    @Test
    void noConsultaNiGuardaSiLosDatosSonInvalidos() {
        User invalido = usuarioValido().correoElectronico("correo-malo").build();

        assertThrows(DatosInvalidosException.class, () -> useCase.registrar(invalido));
        assertEquals(0, repository.consultas);
        assertTrue(repository.guardados.isEmpty());
    }

    @Test
    void guardaElUsuarioNormalizado() {
        useCase.registrar(usuarioValido().documentoIdentidad(" ").build());

        User guardado = repository.guardados.get(0);
        assertEquals("juan@correo.com", guardado.getCorreoElectronico());
        assertEquals(null, guardado.getDocumentoIdentidad());
    }

    @Test
    void rechazaCorreoYaRegistrado() {
        useCase.registrar(usuarioValido().build());
        User mismoCorreo = usuarioValido().documentoIdentidad("99887766").build();

        assertThrows(CorreoYaRegistradoException.class, () -> useCase.registrar(mismoCorreo));
        assertEquals(1, repository.guardados.size());
    }

    @Test
    void rechazaDocumentoYaRegistrado() {
        useCase.registrar(usuarioValido().build());
        User mismoDocumento = usuarioValido().correoElectronico("otro@correo.com").build();

        assertThrows(DocumentoYaRegistradoException.class, () -> useCase.registrar(mismoDocumento));
        assertEquals(1, repository.guardados.size());
    }

    private static class FakeUserRepository implements UserRepository {
        private final List<User> guardados = new ArrayList<>();
        private int consultas;

        @Override
        public User save(User user) {
            User guardado = user.toBuilder().idUsuario((long) guardados.size() + 1).build();
            guardados.add(guardado);
            return guardado;
        }

        @Override
        public boolean existsByCorreoElectronico(String correoElectronico) {
            consultas++;
            return guardados.stream().anyMatch(u -> correoElectronico.equals(u.getCorreoElectronico()));
        }

        @Override
        public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
            consultas++;
            return guardados.stream().anyMatch(u -> documentoIdentidad.equals(u.getDocumentoIdentidad()));
        }
    }
}

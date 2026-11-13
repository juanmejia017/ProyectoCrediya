package co.com.crediya.jpa;

import co.com.crediya.jpa.repository.UserJpaRepository;
import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private UserRepositoryAdapter adapter;

    private User user;
    private UserEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new UserRepositoryAdapter(repository, objectMapper);

        user = User.builder()
                .nombres("Juan")
                .apellidos("Mejia")
                .correoElectronico("juan@correo.com")
                .salarioBase(new BigDecimal("2500000"))
                .build();

        entity = UserEntity.builder()
                .idUsuario(1L)
                .nombres("Juan")
                .apellidos("Mejia")
                .correoElectronico("juan@correo.com")
                .salarioBase(new BigDecimal("2500000"))
                .build();
    }

    @Test
    void testSave() {
        when(repository.save(any(UserEntity.class))).thenReturn(entity);

        User result = adapter.save(user);

        assertEquals(1L, result.getIdUsuario());
        assertEquals("juan@correo.com", result.getCorreoElectronico());
    }

    @Test
    void testSaveDuplicatedEmailThrowsBusinessException() {
        when(repository.save(any(UserEntity.class))).thenThrow(violacion(UserEntity.UK_CORREO));

        assertThrows(CorreoYaRegistradoException.class, () -> adapter.save(user));
    }

    @Test
    void testSaveDuplicatedDocumentThrowsBusinessException() {
        when(repository.save(any(UserEntity.class))).thenThrow(violacion(UserEntity.UK_DOCUMENTO));

        assertThrows(DocumentoYaRegistradoException.class, () -> adapter.save(user));
    }

    @Test
    void testSaveOtherIntegrityViolationIsRethrown() {
        when(repository.save(any(UserEntity.class))).thenThrow(violacion("otra_restriccion"));

        assertThrows(DataIntegrityViolationException.class, () -> adapter.save(user));
    }

    @Test
    void testExistsByCorreoElectronico() {
        when(repository.existsByCorreoElectronico("juan@correo.com")).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico("juan@correo.com"));
    }

    @Test
    void testExistsByDocumentoIdentidad() {
        when(repository.existsByDocumentoIdentidad("1020304050")).thenReturn(true);

        assertTrue(adapter.existsByDocumentoIdentidad("1020304050"));
    }

    private static DataIntegrityViolationException violacion(String restriccion) {
        return new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("ERROR: duplicate key value violates unique constraint \"" + restriccion + "\""));
    }
}

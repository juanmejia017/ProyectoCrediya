package co.com.crediya.jpa.helper;

import co.com.crediya.jpa.JPARepository;
import co.com.crediya.jpa.JPARepositoryAdapter;
import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private JPARepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private JPARepositoryAdapter adapter;

    private User user;
    private UserEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JPARepositoryAdapter(repository, objectMapper);

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
        when(repository.save(any(UserEntity.class))).thenThrow(new DataIntegrityViolationException("duplicado"));

        assertThrows(CorreoYaRegistradoException.class, () -> adapter.save(user));
    }

    @Test
    void testExistsByCorreoElectronico() {
        when(repository.existsByCorreoElectronico("juan@correo.com")).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico("juan@correo.com"));
    }

    @Test
    void testSaveAllEntities() {
        when(repository.<UserEntity>saveAll(ArgumentMatchers.<UserEntity>anyList())).thenReturn(List.of(entity, entity));

        List<User> result = adapter.saveAllEntities(List.of(user, user));

        assertEquals(2, result.size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        User result = adapter.findById(1L);

        assertEquals(1L, result.getIdUsuario());
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(entity, entity));

        List<User> result = adapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testFindByExample() {
        when(repository.findAll(ArgumentMatchers.<Example<UserEntity>>any())).thenReturn(List.of(entity));

        List<User> result = adapter.findByExample(user);

        assertEquals(1, result.size());
    }
}

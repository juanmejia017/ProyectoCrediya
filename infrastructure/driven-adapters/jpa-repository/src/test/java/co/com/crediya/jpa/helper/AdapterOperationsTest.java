package co.com.crediya.jpa.helper;

import co.com.crediya.jpa.UserRepositoryAdapter;
import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.jpa.repository.UserJpaRepository;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las operaciones genéricas de AdapterOperations, usando el adaptador de usuarios.
 */
class AdapterOperationsTest {

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

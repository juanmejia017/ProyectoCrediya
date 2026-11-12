package co.com.crediya.api;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {

    private static final String BODY = """
            {
              "nombres": "Juan",
              "apellidos": "Mejía",
              "fechaNacimiento": "2000-07-12",
              "direccion": "Calle 10 # 20-30",
              "telefono": "3001234567",
              "correoElectronico": "juan@correo.com",
              "salarioBase": 2500000
            }
            """;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UserUseCase userUseCase;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void shouldRegisterUserAndReturn201() {
        User creado = User.builder()
                .idUsuario(1L).nombres("Juan").apellidos("Mejía")
                .correoElectronico("juan@correo.com").salarioBase(new BigDecimal("2500000"))
                .build();
        when(userUseCase.registrar(any())).thenReturn(creado);

        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v1/usuarios/1")
                .expectBody()
                .jsonPath("$.idUsuario").isEqualTo(1);
    }

    @Test
    void shouldReturn400WhenDataIsInvalid() {
        when(userUseCase.registrar(any()))
                .thenThrow(new DatosInvalidosException(List.of("El campo nombres es obligatorio")));

        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("DATOS_INVALIDOS");
    }

    @Test
    void shouldReturn409WhenEmailExists() {
        when(userUseCase.registrar(any()))
                .thenThrow(new CorreoYaRegistradoException());

        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void shouldReturn409WhenDocumentExists() {
        when(userUseCase.registrar(any())).thenThrow(new DocumentoYaRegistradoException());

        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("DOCUMENTO_YA_REGISTRADO");
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() {
        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{ \"salarioBase\": \"abc\" }")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("JSON_INVALIDO");
    }

    @Test
    void shouldReturn415WhenContentTypeIsNotJson() {
        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.TEXT_PLAIN)
                .body("texto")
                .exchange()
                .expectStatus().isEqualTo(415)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("TIPO_CONTENIDO_NO_SOPORTADO");
    }

    @Test
    void shouldReturn500WithGenericMessageOnUnexpectedError() {
        when(userUseCase.registrar(any())).thenThrow(new IllegalStateException("detalle interno"));

        client.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("ERROR_INTERNO");
    }
}

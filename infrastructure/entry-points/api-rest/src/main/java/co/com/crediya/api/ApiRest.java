package co.com.crediya.api;

import co.com.crediya.api.dto.RegistrarUsuarioRequest;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = ApiRest.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {

    static final String BASE_PATH = "/api/v1/usuarios";

    private final UserUseCase userUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody RegistrarUsuarioRequest request) {
        log.info("POST {} - inicio registro de usuario", BASE_PATH);
        User creado = userUseCase.registrar(request.toDomain());
        log.info("POST {} - usuario registrado con id {}", BASE_PATH, creado.getIdUsuario());
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + creado.getIdUsuario()))
                .body(UsuarioResponse.fromDomain(creado));
    }
}

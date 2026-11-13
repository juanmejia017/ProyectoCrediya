package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.jpa.repository.UserJpaRepository;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Repository
public class UserRepositoryAdapter extends AdapterOperations<User, UserEntity, Long, UserJpaRepository>
        implements UserRepository {

    public UserRepositoryAdapter(UserJpaRepository repository, ObjectMapper mapper) {
        super(repository, mapper, UserRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Persistiendo nuevo usuario");
        try {
            User guardado = super.save(user);
            log.info("Usuario persistido con id {}", guardado.getIdUsuario());
            return guardado;
        } catch (DataIntegrityViolationException e) {
            String causa = String.valueOf(e.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT);
            if (causa.contains(UserEntity.UK_CORREO)) {
                log.warn("Restricción única violada: {}", UserEntity.UK_CORREO);
                throw new CorreoYaRegistradoException();
            }
            if (causa.contains(UserEntity.UK_DOCUMENTO)) {
                log.warn("Restricción única violada: {}", UserEntity.UK_DOCUMENTO);
                throw new DocumentoYaRegistradoException();
            }
            log.error("Violación de integridad no controlada al guardar el usuario", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
        return repository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    protected UserEntity toData(User user) {
        return UserEntity.builder()
                .idUsuario(user.getIdUsuario())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getCorreoElectronico())
                .documentoIdentidad(user.getDocumentoIdentidad())
                .idRol(user.getIdRol())
                .salarioBase(user.getSalarioBase())
                .build();
    }

    private static User toDomain(UserEntity entity) {
        return User.builder()
                .idUsuario(entity.getIdUsuario())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .correoElectronico(entity.getCorreoElectronico())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .idRol(entity.getIdRol())
                .salarioBase(entity.getSalarioBase())
                .build();
    }
}

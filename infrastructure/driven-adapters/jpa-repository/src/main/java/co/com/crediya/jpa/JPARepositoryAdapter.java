package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class JPARepositoryAdapter extends AdapterOperations<User, UserEntity, Long, JPARepository>
        implements UserRepository {

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, JPARepositoryAdapter::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Persistiendo usuario con correo {}", user.getCorreoElectronico());
        try {
            User guardado = super.save(user);
            log.info("Usuario persistido con id {}", guardado.getIdUsuario());
            return guardado;
        } catch (DataIntegrityViolationException e) {
            log.warn("Violación de integridad al guardar el correo {}", user.getCorreoElectronico());
            throw new CorreoYaRegistradoException(user.getCorreoElectronico());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
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
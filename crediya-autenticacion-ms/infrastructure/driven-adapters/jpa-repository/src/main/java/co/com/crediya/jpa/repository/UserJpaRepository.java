package co.com.crediya.jpa.repository;

import co.com.crediya.jpa.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface UserJpaRepository extends CrudRepository<UserEntity, Long>, QueryByExampleExecutor<UserEntity> {
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}

package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.transaction.gateways.TransactionalGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.UserValidator;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final TransactionalGateway transactionalGateway;

    public User registrar(User user) {
        UserValidator.validar(user);
        User normalizado = user.normalizado();
        return transactionalGateway.execute(() -> guardarSiNoExiste(normalizado));
    }

    private User guardarSiNoExiste(User user) {
        validarCorreoDisponible(user);
        validarDocumentoDisponible(user);
        return userRepository.save(user);
    }

    private void validarCorreoDisponible(User user) {
        if (userRepository.existsByCorreoElectronico(user.getCorreoElectronico())) {
            throw new CorreoYaRegistradoException();
        }
    }

    private void validarDocumentoDisponible(User user) {
        if (user.getDocumentoIdentidad() != null
                && userRepository.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            throw new DocumentoYaRegistradoException();
        }
    }
}

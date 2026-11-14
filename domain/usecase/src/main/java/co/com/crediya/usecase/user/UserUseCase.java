package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.UserValidator;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public User registrar(User user) {
        UserValidator.validar(user);
        User normalizado = user.normalizado();
        validarCorreoDisponible(normalizado);
        validarDocumentoDisponible(normalizado);
        return userRepository.save(normalizado);
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

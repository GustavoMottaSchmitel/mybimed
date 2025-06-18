package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.exception.ResourceAlreadyExistsException;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel createUser(UserModel user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("Este email ja esta cadastrado.");
        }

        return userRepository.save(user);
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public UserModel updateUser(UUID id, UserModel updateUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setNome(updateUser.getNome());
                    user.setTelefone(updateUser.getTelefone());
                    user.setFotoPerfil(updateUser.getFotoPerfil());
                    user.setCargo(updateUser.getCargo());

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            new ResourceNotFoundException("Usuario não encontrado");
        }
        userRepository.deleteById(id);
    }
}

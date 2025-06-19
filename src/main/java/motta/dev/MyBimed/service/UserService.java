package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.exception.ResourceAlreadyExistsException;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel createUser(UserModel user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("Este email já está cadastrado.");
        }

        return userRepository.save(user);
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public UserModel updateUser(String id, UserModel updateUser) {
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

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }
}
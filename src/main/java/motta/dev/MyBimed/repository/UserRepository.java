package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<UserModel, UUID> {

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

    // Buscar usuário por telefone (caso utilize como identificador de WhatsApp, por exemplo)
    Optional<UserModel> findByTelefone(String telefone);
}

package tn.iset.smartevent.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.User;

// Accès à la table users. Spring Data génère les requêtes à partir du nom des méthodes.
public interface UserRepository extends JpaRepository<User, Integer> {
    // Recherche par email : sert à la connexion et à vérifier qu'un email n'est pas déjà pris.
    Optional<User> findByEmail(String email);
}

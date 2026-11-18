package tn.iset.smartevent.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}

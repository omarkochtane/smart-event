package tn.iset.smartevent.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
}

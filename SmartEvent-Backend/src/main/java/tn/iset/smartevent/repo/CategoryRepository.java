package tn.iset.smartevent.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.Category;

// Accès à la table categories.
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Recherche par libellé : évite de créer deux fois la même catégorie.
    Optional<Category> findByName(String name);
}

package tn.iset.smartevent.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.Registration;

// Accès à la table registrations (inscriptions des étudiants aux événements).
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    // RG3 : vérifie qu'un étudiant n'est pas déjà inscrit à cet événement.
    boolean existsByUserIdAndEventId(Integer userId, Integer eventId);
    // RG2 et RG8 : nombre d'inscrits, pour l'affichage "23 / 30" et le test "complet".
    int countByEventId(Integer eventId);
    // Inscriptions d'un étudiant (écran "Mes inscriptions").
    List<Registration> findByUserId(Integer userId);
    // Inscriptions d'un événement (liste des participants, T5).
    List<Registration> findByEventId(Integer eventId);
    // Suppressions : annulation d'une inscription, suppression d'un événement ou d'un compte.
    void deleteByUserIdAndEventId(Integer userId, Integer eventId);
    void deleteByEventId(Integer eventId);
    void deleteByUserId(Integer userId);
}

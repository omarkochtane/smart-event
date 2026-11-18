package tn.iset.smartevent.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.Event;

// Accès à la table events.
public interface EventRepository extends JpaRepository<Event, Integer> {
    // Événements d'un statut donné : "VALIDE" pour la liste publique (RG1).
    List<Event> findByStatus(String status);
    // Événements créés par un enseignant donné (RG6).
    List<Event> findByOwnerEmail(String ownerEmail);
}

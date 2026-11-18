package tn.iset.smartevent.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByStatus(String status);
    List<Event> findByOwnerEmail(String ownerEmail);
}

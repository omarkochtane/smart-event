package tn.iset.smartevent.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.smartevent.model.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    boolean existsByUserIdAndEventId(Integer userId, Integer eventId);
    int countByEventId(Integer eventId);
    List<Registration> findByUserId(Integer userId);
    List<Registration> findByEventId(Integer eventId);
    void deleteByUserIdAndEventId(Integer userId, Integer eventId);
    void deleteByEventId(Integer eventId);
    void deleteByUserId(Integer userId);
}

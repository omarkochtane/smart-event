package tn.iset.smartevent.web;

import org.springframework.stereotype.Component;
import tn.iset.smartevent.dto.Dtos.EventDto;
import tn.iset.smartevent.model.Event;
import tn.iset.smartevent.repo.RegistrationRepository;

// Convertit une entité Event (base de données) en EventDto (JSON envoyé à l'application).
// Le nombre d'inscrits n'est pas stocké : il est recalculé à chaque conversion.
@Component
public class EventMapper {
    private final RegistrationRepository regs;

    public EventMapper(RegistrationRepository regs) { this.regs = regs; }

    // registered = nombre d'inscrits, calculé depuis la table registrations
    public EventDto toDto(Event e) {
        return new EventDto(e.id, e.title, e.description, e.date, e.time, e.location, e.category,
                e.maxParticipants, regs.countByEventId(e.id), e.status, e.ownerEmail, e.image);
    }
}

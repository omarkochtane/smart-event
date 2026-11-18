package tn.iset.smartevent.web;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.smartevent.dto.Dtos.CategoryDto;
import tn.iset.smartevent.dto.Dtos.EventDto;
import tn.iset.smartevent.model.Event;
import tn.iset.smartevent.model.Registration;
import tn.iset.smartevent.model.User;
import tn.iset.smartevent.repo.CategoryRepository;
import tn.iset.smartevent.repo.EventRepository;
import tn.iset.smartevent.repo.RegistrationRepository;
import tn.iset.smartevent.repo.UserRepository;

// Écrans publics et espace étudiant (E3 à E9).
// La consultation des événements validés ne demande pas de compte ;
// les inscriptions exigent un jeton d'étudiant.
@RestController
@RequestMapping("/api")
public class EventController {
    private final EventRepository events;
    private final RegistrationRepository regs;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final EventMapper mapper;

    public EventController(EventRepository events, RegistrationRepository regs, UserRepository users,
                           CategoryRepository categories, EventMapper mapper) {
        this.events = events; this.regs = regs; this.users = users;
        this.categories = categories; this.mapper = mapper;
    }

    // Liste des catégories (entité Category) : filtre de recherche et formulaire
    @GetMapping("/categories")
    public List<CategoryDto> categories() {
        return categories.findAll().stream().map(c -> new CategoryDto(c.id, c.name)).toList();
    }

    // Seuls les événements VALIDÉS sont publics
    @GetMapping("/events")
    public List<EventDto> publicEvents() {
        return events.findByStatus("VALIDE").stream().map(mapper::toDto).toList();
    }

    // E5 : détails d'un événement (seulement s'il est validé)
    @GetMapping("/events/{id}")
    public EventDto one(@PathVariable Integer id) {
        Event e = events.findById(id).filter(x -> "VALIDE".equals(x.status))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDto(e);
    }

    // Une seule inscription par événement, refusée si complet
    @PostMapping("/events/{id}/register")
    @Transactional
    public void register(@PathVariable Integer id, Principal p) {
        Event e = events.findById(id).filter(x -> "VALIDE".equals(x.status))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User u = me(p);
        if (regs.existsByUserIdAndEventId(u.id, id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Déjà inscrit");
        if (regs.countByEventId(id) >= e.maxParticipants)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Événement complet");
        regs.save(new Registration(u.id, id));
    }

    @DeleteMapping("/events/{id}/register")
    @Transactional
    public void unregister(@PathVariable Integer id, Principal p) {
        regs.deleteByUserIdAndEventId(me(p).id, id);
    }

    @GetMapping("/me/registrations")
    public List<EventDto> myRegistrations(Principal p) {
        return regs.findByUserId(me(p).id).stream()
                .map(r -> events.findById(r.eventId).orElse(null))
                .filter(e -> e != null)
                .map(mapper::toDto).toList();
    }

    private User me(Principal p) {
        return users.findByEmail(p.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}

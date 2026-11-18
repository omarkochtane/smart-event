package tn.iset.smartevent.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.smartevent.dto.Dtos.*;
import tn.iset.smartevent.model.Event;
import tn.iset.smartevent.model.User;
import tn.iset.smartevent.repo.EventRepository;
import tn.iset.smartevent.repo.RegistrationRepository;
import tn.iset.smartevent.repo.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final EventRepository events;
    private final RegistrationRepository regs;
    private final EventMapper mapper;

    public AdminController(UserRepository users, EventRepository events, RegistrationRepository regs, EventMapper mapper) {
        this.users = users; this.events = events; this.regs = regs; this.mapper = mapper;
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return users.findAll().stream().map(u -> new UserDto(u.id, u.name, u.email, u.role, u.photo, u.phone)).toList();
    }

    @DeleteMapping("/users/{id}")
    @Transactional
    public void deleteUser(@PathVariable Integer id) {
        User u = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if ("ADMIN".equals(u.role))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible de supprimer un administrateur");
        regs.deleteByUserId(id);
        users.deleteById(id);
    }

    // L'administrateur choisit si un utilisateur est ÉTUDIANT ou ENSEIGNANT
    @PutMapping("/users/{id}/role")
    public void setRole(@PathVariable Integer id, @RequestBody RoleRequest r) {
        if (!List.of("STUDENT", "TEACHER").contains(r.role()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle invalide");
        User u = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if ("ADMIN".equals(u.role))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le rôle d'un administrateur ne change pas");
        u.role = r.role();
        users.save(u);
    }

    @GetMapping("/events")
    public List<EventDto> allEvents() {
        return events.findAll().stream().map(mapper::toDto).toList();
    }

    // Valider ou refuser un événement
    @PutMapping("/events/{id}/status")
    public void setStatus(@PathVariable Integer id, @RequestBody StatusRequest r) {
        if (!List.of("VALIDE", "REFUSE", "EN_ATTENTE").contains(r.status()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide");
        Event e = events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        e.status = r.status();
        events.save(e);
    }

    // L'administrateur peut créer un événement : il est directement VALIDÉ
    @PostMapping("/events")
    public EventDto create(@RequestBody EventDto d, java.security.Principal p) {
        if (d.title() == null || d.title().isBlank() || d.maxParticipants() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données invalides");
        Event e = new Event();
        e.title = d.title(); e.description = d.description(); e.date = d.date(); e.time = d.time();
        e.location = d.location(); e.category = d.category(); e.maxParticipants = d.maxParticipants();
        e.image = d.image();
        e.status = "VALIDE";
        e.ownerEmail = p.getName();
        return mapper.toDto(events.save(e));
    }
}

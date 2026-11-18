package tn.iset.smartevent.web;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.smartevent.dto.Dtos.*;
import tn.iset.smartevent.model.Event;
import tn.iset.smartevent.repo.EventRepository;
import tn.iset.smartevent.repo.RegistrationRepository;
import tn.iset.smartevent.repo.UserRepository;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final EventRepository events;
    private final RegistrationRepository regs;
    private final UserRepository users;
    private final EventMapper mapper;

    public TeacherController(EventRepository events, RegistrationRepository regs, UserRepository users, EventMapper mapper) {
        this.events = events; this.regs = regs; this.users = users; this.mapper = mapper;
    }

    @GetMapping("/events")
    public List<EventDto> mine(Principal p) {
        return events.findByOwnerEmail(p.getName()).stream().map(mapper::toDto).toList();
    }

    // Un nouvel événement est toujours EN_ATTENTE de validation par l'admin
    @PostMapping("/events")
    public EventDto create(@RequestBody EventDto d, Principal p) {
        if (d.title() == null || d.title().isBlank() || d.maxParticipants() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données invalides");
        Event e = new Event();
        fill(e, d);
        e.status = "EN_ATTENTE";
        e.ownerEmail = p.getName();
        return mapper.toDto(events.save(e));
    }

    @PutMapping("/events/{id}")
    public EventDto update(@PathVariable Integer id, @RequestBody EventDto d, Principal p) {
        Event e = owned(id, p);
        if (d.maxParticipants() < regs.countByEventId(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum inférieur au nombre d'inscrits");
        fill(e, d);
        // un événement refusé, une fois modifié, repart en validation
        if ("REFUSE".equals(e.status)) e.status = "EN_ATTENTE";
        return mapper.toDto(events.save(e));
    }

    @DeleteMapping("/events/{id}")
    @Transactional
    public void delete(@PathVariable Integer id, Principal p) {
        owned(id, p);
        regs.deleteByEventId(id);
        events.deleteById(id);
    }

    @GetMapping("/events/{id}/participants")
    public List<UserDto> participants(@PathVariable Integer id, Principal p) {
        owned(id, p);
        return regs.findByEventId(id).stream()
                .map(r -> users.findById(r.userId).orElse(null))
                .filter(u -> u != null)
                .map(u -> new UserDto(u.id, u.name, u.email, u.role, u.photo, u.phone)).toList();
    }

    private Event owned(Integer id, Principal p) {
        Event e = events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!p.getName().equals(e.ownerEmail)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return e;
    }

    private void fill(Event e, EventDto d) {
        e.title = d.title();
        e.description = d.description();
        e.date = d.date();
        e.time = d.time();
        e.location = d.location();
        e.category = d.category();
        e.maxParticipants = d.maxParticipants();
        e.image = d.image();
    }
}

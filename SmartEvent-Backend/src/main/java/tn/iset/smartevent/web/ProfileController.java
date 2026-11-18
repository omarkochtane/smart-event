package tn.iset.smartevent.web;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.smartevent.dto.Dtos.*;
import tn.iset.smartevent.model.User;
import tn.iset.smartevent.repo.UserRepository;

// Profil de l'utilisateur connecté (tous les rôles)
// Profil de l'utilisateur connecté (tous les rôles) : consultation et photo.
@RestController
@RequestMapping("/api/me")
public class ProfileController {
    private final UserRepository users;

    public ProfileController(UserRepository users) { this.users = users; }

    @GetMapping
    public UserDto me(Principal p) {
        User u = find(p);
        return new UserDto(u.id, u.name, u.email, u.role, u.photo, u.phone);
    }

    // Photo de profil : image JPEG en base64 (null ou vide = supprimer)
    @PutMapping("/photo")
    public void photo(@RequestBody PhotoRequest r, Principal p) {
        User u = find(p);
        u.photo = (r.photo() == null || r.photo().isBlank()) ? null : r.photo();
        users.save(u);
    }

    private User find(Principal p) {
        return users.findByEmail(p.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}

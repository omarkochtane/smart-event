package tn.iset.smartevent.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.smartevent.dto.Dtos.*;
import tn.iset.smartevent.model.User;
import tn.iset.smartevent.repo.UserRepository;
import tn.iset.smartevent.security.JwtService;

// Inscription, connexion et informations du compte.
// Ces URL sont les seules accessibles sans jeton (voir SecurityConfig).
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final java.util.Set<String> adminEmails;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt,
                          @Value("${app.admin-emails}") String admins) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
        this.adminEmails = java.util.Arrays.stream(admins.split(",")).map(x -> x.trim().toLowerCase())
                .filter(x -> !x.isEmpty()).collect(java.util.stream.Collectors.toSet());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.passwordHash))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));
        return response(promoteIfAdmin(u));
    }

    // L'inscription crée un compte ÉTUDIANT (sauf les emails administrateurs).
    // C'est ensuite l'administrateur qui choisit qui est enseignant.
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (r.name().isBlank() || email.isBlank() || r.password().length() < 4)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données invalides");
        String phone = r.phone() == null ? "" : r.phone().replaceAll("[\\s.-]", "");
        if (!phone.matches("\\+?[0-9]{8,15}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numéro de téléphone invalide");
        if (users.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        User nu = new User(r.name().trim(), email, encoder.encode(r.password()),
                adminEmails.contains(email) ? "ADMIN" : "STUDENT");
        nu.phone = phone;
        User u = users.save(nu);
        return response(u);
    }

    // Adresses IP du PC (pour régler BASE_URL dans l'app Android)
    @GetMapping("/info")
    public java.util.List<String> info() throws Exception {
        java.util.List<String> ips = new java.util.ArrayList<>();
        for (var ni : java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || ni.isLoopback()) continue;
            for (var a : java.util.Collections.list(ni.getInetAddresses()))
                if (a instanceof java.net.Inet4Address) ips.add(ni.getDisplayName() + " -> " + a.getHostAddress());
        }
        return ips;
    }

    // Les emails administrateurs ont toujours le rôle ADMIN
    private User promoteIfAdmin(User u) {
        if (adminEmails.contains(u.email) && !"ADMIN".equals(u.role)) {
            u.role = "ADMIN";
            return users.save(u);
        }
        return u;
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwt.generate(u.email, u.role), u.id, u.name, u.email, u.role, u.photo);
    }
}

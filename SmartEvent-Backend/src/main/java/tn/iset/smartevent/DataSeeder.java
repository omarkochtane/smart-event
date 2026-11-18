package tn.iset.smartevent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.iset.smartevent.model.Category;
import tn.iset.smartevent.model.Event;
import tn.iset.smartevent.model.Registration;
import tn.iset.smartevent.model.User;
import tn.iset.smartevent.repo.CategoryRepository;
import tn.iset.smartevent.repo.EventRepository;
import tn.iset.smartevent.repo.RegistrationRepository;
import tn.iset.smartevent.repo.UserRepository;

// Données de démonstration (seulement si la base est vide)
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final EventRepository events;
    private final RegistrationRepository regs;
    private final CategoryRepository categories;
    private final PasswordEncoder enc;

    public DataSeeder(UserRepository users, EventRepository events, RegistrationRepository regs,
                      CategoryRepository categories, PasswordEncoder enc) {
        this.users = users; this.events = events; this.regs = regs;
        this.categories = categories; this.enc = enc;
    }

    @Override
    public void run(String... args) {
        // Catégories d'événements (créées une seule fois)
        if (categories.count() == 0) {
            for (String nom : new String[]{"Informatique", "Conférence", "Séminaire", "Sport", "Club"}) {
                categories.save(new Category(nom));
            }
        }

        // ancien compte admin de démonstration : supprimé (les admins sont définis dans application.properties)
        users.findByEmail("admin@iset.tn").ifPresent(users::delete);
        if (users.count() > 0) return;

        users.save(new User("Prof Ahmed", "prof.ahmed@iset.tn", enc.encode("prof123"), "TEACHER"));
        users.save(new User("Prof Leila", "prof.leila@iset.tn", enc.encode("prof123"), "TEACHER"));
        String[][] students = {
            {"Amira Ben Salah", "amira.ben@iset.tn"}, {"Mohamed Trabelsi", "mohamed.trabelsi@iset.tn"},
            {"Sarra Jlassi", "sarra.jlassi@iset.tn"}, {"Yassine Souissi", "yassine.souissi@iset.tn"},
            {"Nour Gharbi", "nour.gharbi@iset.tn"}, {"Hedi Mansouri", "hedi.mansouri@iset.tn"}
        };
        for (String[] s : students) users.save(new User(s[0], s[1], enc.encode("etud123"), "STUDENT"));

        Event a = ev("Workshop Angular", "Découvrez les bases et les bonnes pratiques avec des cas concrets.", "20/11/2026", "10:00", "Salle A101", "Informatique", 30, "VALIDE", "prof.ahmed@iset.tn");
        Event b = ev("Conférence IA et Société", "L'impact de l'intelligence artificielle sur la société et les métiers de demain.", "25/11/2026", "14:00", "Amphi B", "Conférence", 120, "VALIDE", "prof.ahmed@iset.tn");
        ev("Séminaire Cybersécurité", "Sensibilisation aux risques informatiques et aux bonnes pratiques.", "02/12/2026", "09:30", "Salle C204", "Séminaire", 40, "VALIDE", "prof.leila@iset.tn");
        ev("Tournoi de Football", "Tournoi inter-classes organisé par le club sportif.", "05/12/2026", "15:00", "Terrain de sport", "Sport", 60, "VALIDE", "prof.leila@iset.tn");
        ev("Hackathon Mobile", "24 heures pour concevoir une application mobile par équipe de trois.", "12/12/2026", "08:00", "Salle B12", "Informatique", 50, "EN_ATTENTE", "prof.ahmed@iset.tn");
        ev("Journée Portes Ouvertes", "Présentation des filières de l'institut aux futurs étudiants.", "15/12/2026", "09:00", "Hall principal", "Club", 200, "REFUSE", "prof.leila@iset.tn");

        // quelques inscriptions de démonstration
        for (User u : users.findAll()) {
            if ("STUDENT".equals(u.role)) {
                regs.save(new Registration(u.id, a.id));
                regs.save(new Registration(u.id, b.id));
            }
        }
    }

    private Event ev(String title, String desc, String date, String time, String loc, String cat, int max, String status, String owner) {
        Event e = new Event();
        e.title = title; e.description = desc; e.date = date; e.time = time; e.location = loc;
        e.category = cat; e.maxParticipants = max; e.status = status; e.ownerEmail = owner;
        return events.save(e);
    }
}

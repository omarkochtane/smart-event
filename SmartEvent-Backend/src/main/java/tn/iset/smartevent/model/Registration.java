package tn.iset.smartevent.model;

import jakarta.persistence.*;

// Une seule inscription par (utilisateur, événement)
@Entity
@Table(name = "registrations", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "eventId"}))
public class Registration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public Integer userId;
    public Integer eventId;
    // date de l'inscription (jj/mm/aaaa)
    public String registrationDate;

    public Registration() {}
    public Registration(Integer userId, Integer eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

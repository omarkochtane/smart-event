package tn.iset.smartevent.model;

import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String title;
    @Column(length = 2000)
    public String description;
    @Column(name = "event_date")
    public String date;
    @Column(name = "event_time")
    public String time;
    public String location;
    public String category;
    public int maxParticipants;
    // EN_ATTENTE | VALIDE | REFUSE
    public String status;
    public String ownerEmail;
    // affiche de l'événement (image JPEG encodée en base64)
    @Lob
    public String image;

    public Event() {}
}

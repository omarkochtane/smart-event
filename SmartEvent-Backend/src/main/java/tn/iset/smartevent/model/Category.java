package tn.iset.smartevent.model;

import jakarta.persistence.*;

// Catégorie d'événement (Informatique, Conférence, Séminaire, Sport, Club...).
// Une catégorie regroupe plusieurs événements : Event.category reprend son libellé.
@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, unique = true)
    public String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }
}

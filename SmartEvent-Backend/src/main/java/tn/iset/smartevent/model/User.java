package tn.iset.smartevent.model;

import jakarta.persistence.*;

// Utilisateur de l'application : étudiant, enseignant ou administrateur.
// Le mot de passe n'est jamais stocké en clair : seule son empreinte BCrypt est enregistrée.
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;
    @Column(unique = true, nullable = false)
    public String email;
    @Column(name = "password_hash", nullable = false)
    public String passwordHash;
    // STUDENT | TEACHER | ADMIN
    public String role;
    public String phone;
    // photo de profil (image JPEG encodée en base64)
    @Lob
    public String photo;

    public User() {}
    public User(String name, String email, String passwordHash, String role) {
        this.name = name; this.email = email; this.passwordHash = passwordHash; this.role = role;
    }
}

-- =====================================================================
-- SMART EVENT - Base de données MySQL
-- Projet universitaire ISET Kélibia (L3 MDW) - Groupe appmobG3
-- Omar Kochtane & Yassine Souissi
--
-- Ce script est fourni comme livrable (schéma de la base).
-- Au démarrage du backend, Hibernate crée les mêmes tables
-- automatiquement (spring.jpa.hibernate.ddl-auto=update).
--
-- Utilisation : mysql -u root -p < schema.sql
--           ou : ouvrir ce fichier dans MySQL Workbench et l'exécuter.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS smartevent
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartevent;

-- Suppression dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- Table users : étudiants, enseignants et administrateurs
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255)                          NOT NULL,
    email         VARCHAR(255)                          NOT NULL UNIQUE,
    password_hash VARCHAR(255)                          NOT NULL,  -- BCrypt
    role          ENUM('STUDENT','TEACHER','ADMIN')     NOT NULL DEFAULT 'STUDENT',
    phone         VARCHAR(30),
    photo         LONGTEXT                                         -- JPEG en base64
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Table categories : catégories d'événements
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Table events : les événements universitaires
-- Statuts possibles (RG5) : EN_ATTENTE, VALIDE, REFUSE
-- ---------------------------------------------------------------------
CREATE TABLE events (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255)                           NOT NULL,
    description      VARCHAR(2000),
    event_date       VARCHAR(20),                                      -- jj/mm/aaaa
    event_time       VARCHAR(10),                                      -- hh:mm
    location         VARCHAR(255),
    category         VARCHAR(100),
    max_participants INT                                    NOT NULL DEFAULT 0,
    status           ENUM('EN_ATTENTE','VALIDE','REFUSE')   NOT NULL DEFAULT 'EN_ATTENTE',
    owner_email      VARCHAR(255),                                     -- enseignant organisateur
    image            LONGTEXT,                                         -- affiche JPEG en base64
    CONSTRAINT fk_events_owner FOREIGN KEY (owner_email)
        REFERENCES users (email) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_events_status (status),
    INDEX idx_events_category (category)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Table registrations : inscriptions des étudiants aux événements
-- RG3 : un étudiant ne peut pas s'inscrire deux fois au même événement
-- ---------------------------------------------------------------------
CREATE TABLE registrations (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT NOT NULL,
    event_id          INT NOT NULL,
    registration_date VARCHAR(20),
    CONSTRAINT uk_registration UNIQUE (user_id, event_id),
    CONSTRAINT fk_reg_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
-- Données de démonstration
-- Mots de passe en clair : prof123 (enseignants), etud123 (étudiants).
-- Les empreintes ci-dessous sont des exemples ; le backend régénère
-- ces comptes au premier démarrage si la table users est vide.
-- =====================================================================

INSERT INTO categories (name) VALUES
    ('Informatique'), ('Conférence'), ('Séminaire'), ('Sport'), ('Club');

INSERT INTO users (name, email, password_hash, role, phone) VALUES
    ('Prof Ahmed',       'prof.ahmed@iset.tn',       '$2a$10$exempleBCryptProf1', 'TEACHER', '22100100'),
    ('Prof Leila',       'prof.leila@iset.tn',       '$2a$10$exempleBCryptProf2', 'TEACHER', '22100101'),
    ('Amira Ben Salah',  'amira.ben@iset.tn',        '$2a$10$exempleBCryptEtud1', 'STUDENT', '22200200'),
    ('Mohamed Trabelsi', 'mohamed.trabelsi@iset.tn', '$2a$10$exempleBCryptEtud2', 'STUDENT', '22200201'),
    ('Sarra Jlassi',     'sarra.jlassi@iset.tn',     '$2a$10$exempleBCryptEtud3', 'STUDENT', '22200202'),
    ('Yassine Souissi',  'yassine.souissi@iset.tn',  '$2a$10$exempleBCryptEtud4', 'STUDENT', '22200203');

INSERT INTO events (title, description, event_date, event_time, location, category, max_participants, status, owner_email) VALUES
    ('Workshop Angular',        'Découvrez les bases et les bonnes pratiques avec des cas concrets.',          '20/11/2026', '10:00', 'Salle A101',       'Informatique', 30,  'VALIDE',     'prof.ahmed@iset.tn'),
    ('Conférence IA et Société','L''impact de l''intelligence artificielle sur la société.',                   '25/11/2026', '14:00', 'Amphi B',          'Conférence',   120, 'VALIDE',     'prof.ahmed@iset.tn'),
    ('Séminaire Cybersécurité', 'Sensibilisation aux risques informatiques et aux bonnes pratiques.',          '02/12/2026', '09:30', 'Salle C204',       'Séminaire',    40,  'VALIDE',     'prof.leila@iset.tn'),
    ('Tournoi de Football',     'Tournoi inter-classes organisé par le club sportif.',                         '05/12/2026', '15:00', 'Terrain de sport', 'Sport',        60,  'VALIDE',     'prof.leila@iset.tn'),
    ('Hackathon Mobile',        '24 heures pour concevoir une application mobile par équipe de trois.',        '12/12/2026', '08:00', 'Salle B12',        'Informatique', 50,  'EN_ATTENTE', 'prof.ahmed@iset.tn'),
    ('Journée Portes Ouvertes', 'Présentation des filières de l''institut aux futurs étudiants.',              '15/12/2026', '09:00', 'Hall principal',   'Club',         200, 'REFUSE',     'prof.leila@iset.tn');

INSERT INTO registrations (user_id, event_id, registration_date) VALUES
    (3, 1, '10/11/2026'),
    (4, 1, '11/11/2026'),
    (5, 1, '11/11/2026'),
    (3, 2, '12/11/2026'),
    (6, 2, '12/11/2026');

-- =====================================================================
-- Quelques requêtes utiles
-- =====================================================================

-- Événements visibles par les étudiants (RG1)
-- SELECT * FROM events WHERE status = 'VALIDE';

-- Nombre d'inscrits par événement (RG8 : « 23 / 30 participants »)
-- SELECT e.title, COUNT(r.id) AS inscrits, e.max_participants
-- FROM events e LEFT JOIN registrations r ON r.event_id = e.id
-- GROUP BY e.id, e.title, e.max_participants;

-- Liste des étudiants inscrits à un événement (T5)
-- SELECT u.name, u.email, r.registration_date
-- FROM registrations r JOIN users u ON u.id = r.user_id
-- WHERE r.event_id = 1;

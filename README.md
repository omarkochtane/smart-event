# SMART EVENT

Application mobile de gestion des événements universitaires (ISET Kélibia, L3 MDW).
Groupe **appmobG3** — Omar Kochtane & Yassine Souissi.

Trois rôles : **étudiant** (consulte et s'inscrit), **enseignant** (crée et gère ses événements),
**administrateur** (gère les utilisateurs, valide ou refuse les événements).

## Contenu du dépôt

| Dossier | Description |
|---|---|
| `SmartEvent-Android/` | Application Android (Kotlin, Jetpack Compose, Retrofit) |
| `SmartEvent-Backend/` | API REST (Spring Boot, Spring Security, JWT, JPA, MySQL) |
| `SmartEvent-Backend/database/` | `schema.sql` : script de création de la base MySQL |
| `SmartEvent-Backend/postman/` | Collection Postman de test de l'API |
| `apk/` | `SmartEvent.apk` (se connecte au serveur) et `SmartEvent-demo.apk` (démo hors ligne) |

## Architecture

```
Android (Kotlin, Compose)  ->  API REST  ->  Spring Boot  ->  MySQL
```

Quatre entités : **User**, **Event**, **Registration**, **Category**.
Mots de passe chiffrés en BCrypt, authentification par jeton **JWT**.

## Installer l'application (version connectée au serveur)

1. Télécharger **`apk/SmartEvent.apk`** sur un téléphone Android et l'installer
   (autoriser l'installation depuis une source inconnue).
2. Le téléphone doit être sur le **même Wi-Fi** que le PC qui exécute le backend.
3. Dans l'application : **Connexion → Adresse du serveur**, saisir l'adresse IP du PC
   (visible avec `ipconfig`, par exemple `192.168.1.150`) puis **Enregistrer**.
   Le port `8081` est ajouté automatiquement et l'adresse est mémorisée.
4. Se connecter avec un compte de démonstration ou créer un compte.

Sur le PC : MySQL (Wamp) démarré, `SmartEventApplication` lancé, et le port **8081**
autorisé dans le pare-feu Windows.

## Tester sans serveur (APK démo)

1. Télécharger `apk/SmartEvent-demo.apk` : aucune connexion, données fictives.
2. S'inscrire avec un email fictif (mot de passe ≥ 4 caractères, téléphone ≥ 8 chiffres) :
   - `omarkochtane51@gmail.com` ou `yassinesouissi011@gmail.com` → espace administrateur
   - email contenant `prof` → espace enseignant
   - autre email → espace étudiant

## Lancer la version complète

1. Démarrer MySQL (Wamp / XAMPP). La base `smartevent` est créée automatiquement
   (ou exécuter `SmartEvent-Backend/database/schema.sql`).
2. Ouvrir `SmartEvent-Backend` dans IntelliJ / Android Studio et lancer
   `SmartEventApplication` (port **8081**).
3. Dans `SmartEvent-Android/.../data/api/Api.kt`, régler `BASE_URL` :
   - émulateur Android Studio : `http://10.0.2.2:8081/`
   - téléphone physique sur le même Wi-Fi : `http://<IP-du-PC>:8081/`
4. Lancer l'application depuis Android Studio.

## Comptes de démonstration

| Rôle | Email | Mot de passe |
|---|---|---|
| Enseignant | `prof.ahmed@iset.tn` | `prof123` |
| Étudiant | `amira.ben@iset.tn` | `etud123` |
| Administrateur | `omarkochtane51@gmail.com` | à créer via l'inscription |

Les emails administrateurs sont définis dans
`SmartEvent-Backend/src/main/resources/application.properties` (`app.admin-emails`).

## Tests de l'API

Importer `SmartEvent-Backend/postman/SMART_EVENT.postman_collection.json` dans Postman,
puis exécuter d'abord le dossier **1. Authentification** : il enregistre les jetons JWT
utilisés par les autres requêtes.

## Fonctionnalités

**Étudiant** : créer un compte, se connecter, consulter les événements validés, rechercher
par titre et par catégorie, consulter les détails, s'inscrire, annuler son inscription,
« Mes inscriptions », profil avec photo.

**Enseignant** : ajouter, modifier et supprimer ses événements, consulter la liste des
étudiants inscrits.

**Administrateur** : liste des utilisateurs, choix du rôle (étudiant / enseignant),
suppression d'un utilisateur, consultation de tous les événements, ajout d'un événement,
validation ou refus.

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
2. Se connecter au **Wi-Fi partagé par le PC** (point d'accès sans fil mobile de
   Windows). L'application est préréglée sur `192.168.137.1`, l'adresse que Windows
   donne au PC dans ce cas : **aucune saisie n'est nécessaire**.
3. Se connecter avec un compte de démonstration ou créer un compte.

Sur un Wi-Fi ordinaire (box, réseau de l'établissement), l'adresse du PC est différente :
**Connexion → Adresse du serveur**, saisir l'adresse IPv4 du PC (commande `ipconfig`,
par exemple `192.168.1.22`) puis **Enregistrer**. Le port `8081` est ajouté
automatiquement et l'adresse est mémorisée sur le téléphone.

Sur le PC : MySQL (Wamp) démarré, `SmartEventApplication` lancé, et le port **8081**
autorisé dans le pare-feu Windows.

## Tester sans serveur (APK démo)

1. Télécharger `apk/SmartEvent-demo.apk` : aucune connexion, données fictives.
2. S'inscrire avec un email fictif (mot de passe ≥ 4 caractères, téléphone ≥ 8 chiffres) :
   - `omarkochtane51@gmail.com` ou `yassinesouissi011@gmail.com` → espace administrateur
   - email contenant `prof` → espace enseignant
   - autre email → espace étudiant

## Lancer le projet (à refaire après chaque démarrage du PC)

1. **Démarrer MySQL** : lancer Wamp et attendre que son icône devienne **verte**
   dans la barre des tâches. Sans cela le backend s'arrête avec l'erreur
   `Communications link failure`. La base `smartevent` et ses données sont
   conservées d'une fois sur l'autre (rien à recréer).
2. **Démarrer le backend** : ouvrir `SmartEvent-Backend` dans Android Studio /
   IntelliJ et lancer `SmartEventApplication` (port **8081**). Vérifier dans un
   navigateur que `http://localhost:8081/api/events` renvoie du JSON.
3. **Partager le Wi-Fi** (pour que des téléphones se connectent) :
   *Paramètres → Réseau et Internet → Point d'accès sans fil mobile*. Windows
   donne alors au PC l'adresse `192.168.137.1`, sur laquelle l'application est
   préréglée. Le partage se désactive à chaque extinction du PC : il faut le
   réactiver.
4. **Lancer l'application** : ouvrir `SmartEvent-Android` dans Android Studio,
   choisir l'appareil (émulateur ou téléphone) et cliquer sur ▶.
   - sur l'**émulateur**, l'adresse du serveur est `10.0.2.2` : la saisir une fois
     dans *Connexion → Adresse du serveur* ;
   - sur un **téléphone connecté au Wi-Fi partagé**, il n'y a rien à saisir.

L'adresse saisie est mémorisée sur l'appareil : elle n'est à régler qu'une fois.

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

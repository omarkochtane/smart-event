# Contrat API — SMART EVENT (Android ⇄ Spring Boot)

Base : `http://<IP_PC>:8081/` — JSON — authentification par jeton JWT : `Authorization: Bearer <token>`

| Méthode | URL | Rôle | Corps / Réponse |
|---|---|---|---|
| POST | /api/auth/login | public | {email,password} → {token,id,name,email,role} |
| POST | /api/auth/register | public | {name,email,password} → idem (rôle STUDENT) |
| GET | /api/events | public | événements VALIDE uniquement |
| GET | /api/events/{id} | public | un événement |
| POST | /api/events/{id}/register | STUDENT | RG : 1 seule inscription, refus si complet (409) |
| DELETE | /api/events/{id}/register | STUDENT | désinscription |
| GET | /api/me/registrations | STUDENT | mes événements |
| GET | /api/teacher/events | TEACHER | mes événements (tous statuts) |
| POST | /api/teacher/events | TEACHER | crée avec statut EN_ATTENTE |
| PUT | /api/teacher/events/{id} | TEACHER | modifie (REFUSE → EN_ATTENTE) |
| DELETE | /api/teacher/events/{id} | TEACHER | supprime |
| GET | /api/teacher/events/{id}/participants | TEACHER | liste des inscrits |
| GET | /api/admin/users | ADMIN | tous les utilisateurs |
| DELETE | /api/admin/users/{id} | ADMIN | supprime |
| GET | /api/admin/events | ADMIN | tous les événements |
| PUT | /api/admin/events/{id}/status | ADMIN | {status: VALIDE\|REFUSE} |

Tables MySQL : users(id,name,email,password_hash,role), events(id,title,description,date,time,location,category,max_participants,status,owner_id), registrations(user_id,event_id) avec clé unique (user_id,event_id).
Côté Android : mettre `ApiConfig.USE_API = true` et la bonne `BASE_URL` (fichier data/api/Api.kt).

## Ajouts (admins, photo de profil, affiche)
| Méthode | URL | Rôle | Description |
|---|---|---|---|
| GET | /api/me | connecté | Profil de l'utilisateur connecté |
| PUT | /api/me/photo | connecté | Body `{"photo": "<base64 JPEG>"}` – photo de profil |
| PUT | /api/admin/users/{id}/role | ADMIN | Body `{"role": "STUDENT" ou "TEACHER"}` |

- Les emails admin sont dans `app.admin-emails` (application.properties) : omarkochtane51@gmail.com, yassinesouissi011@gmail.com.
- `EventDto.image` : affiche de l'événement (JPEG base64). `UserDto.photo` / `AuthResponse.photo` : photo de profil.

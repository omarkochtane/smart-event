package tn.iset.smartevent.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tn.iset.smartevent.data.api.ApiClient
import tn.iset.smartevent.data.api.ApiConfig
import tn.iset.smartevent.data.api.EventDto
import tn.iset.smartevent.data.api.LoginRequest
import tn.iset.smartevent.data.api.PhotoRequest
import tn.iset.smartevent.data.api.RoleRequest
import tn.iset.smartevent.data.api.RegisterRequest
import tn.iset.smartevent.data.api.StatusRequest

enum class Role { STUDENT, TEACHER, ADMIN }

data class AppUser(val id: Int, val name: String, val email: String, val role: Role, val photo: String? = null, val phone: String? = null)

// État de l'application.
// ApiConfig.USE_API = false : données fictives en mémoire.
// ApiConfig.USE_API = true  : les données viennent du backend Spring Boot (Retrofit + JWT).
object AppState {
    // Relu à chaque appel : le client est recréé si l'adresse du serveur change.
    private val api get() = ApiClient.service
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val registeredIds = mutableStateListOf<Int>()
    var userName by mutableStateOf("")
    var userEmail by mutableStateOf("")
    var role by mutableStateOf(Role.STUDENT)
    var userPhoto by mutableStateOf<String?>(null)   // photo de profil (JPEG base64)

    // Dernier message d'erreur réseau (affiché si besoin)
    var apiError by mutableStateOf<String?>(null)

    // Tous les événements
    val events = mutableStateListOf<Event>().apply {
        if (!ApiConfig.USE_API) addAll(SampleData.events)
    }

    // Catégories (entité Category côté serveur ; "Tous" sert de filtre « pas de catégorie »)
    val categories = mutableStateListOf<String>().apply { addAll(SampleData.categories) }

    // Utilisateurs (espace admin)
    val users = mutableStateListOf<AppUser>().apply {
        if (!ApiConfig.USE_API) {
            add(AppUser(1, "Amira Ben Salah", "amira.ben@iset.tn", Role.STUDENT))
            add(AppUser(2, "Mohamed Trabelsi", "mohamed.trabelsi@iset.tn", Role.STUDENT))
            add(AppUser(3, "Sarra Jlassi", "sarra.jlassi@iset.tn", Role.STUDENT))
            add(AppUser(4, "Prof Ahmed", "prof.ahmed@iset.tn", Role.TEACHER))
            add(AppUser(5, "Prof Leila", "prof.leila@iset.tn", Role.TEACHER))
            add(AppUser(6, "Admin", "admin@iset.tn", Role.ADMIN))
        }
    }

    // ---------- Mode API ----------
    private fun roleOf(s: String) = runCatching { Role.valueOf(s) }.getOrDefault(Role.STUDENT)

    private fun statusOf(s: String) = runCatching { EventStatus.valueOf(s) }.getOrDefault(EventStatus.EN_ATTENTE)

    private fun EventDto.toEvent() = Event(
        id = id ?: 0, title = title, description = description, date = date, time = time,
        location = location, category = category, maxParticipants = maxParticipants,
        registered = registered, status = statusOf(status), owner = ownerEmail ?: "", image = image
    )

    private fun Event.toDto() = EventDto(
        id = id, title = title, description = description, date = date, time = time,
        location = location, category = category, maxParticipants = maxParticipants,
        status = status.name, ownerEmail = owner, image = image
    )

    private fun errorText(e: Exception): String = when (e) {
        is retrofit2.HttpException -> when (e.code()) {
            401 -> "Email ou mot de passe incorrect"
            409 -> "Action impossible (déjà inscrit, complet ou email déjà utilisé)"
            else -> "Erreur serveur (${e.code()})"
        }
        else -> "Serveur injoignable. Vérifiez l'adresse du serveur, le Wi-Fi et que le backend est lancé."
    }

    // Lance un appel API en arrière-plan, puis recharge les données
    private fun call(block: suspend () -> Unit) {
        scope.launch {
            try {
                block()
                refresh()
                apiError = null
            } catch (e: Exception) {
                apiError = errorText(e)
            }
        }
    }

    // Recharge les données selon le rôle connecté
    suspend fun refresh() {
        if (!ApiConfig.USE_API) return
        val auth = ApiClient.bearer
        val loggedIn = ApiClient.token.isNotEmpty()
        val list = when {
            loggedIn && role == Role.ADMIN -> api.allEvents(auth)
            loggedIn && role == Role.TEACHER -> api.teacherEvents(auth)
            else -> api.publicEvents()
        }
        events.clear()
        events.addAll(list.map { it.toEvent() }.sortedByDescending { it.id })

        // Catégories chargées depuis la table categories
        val noms = api.categories().map { it.name }
        if (noms.isNotEmpty()) {
            categories.clear()
            categories.add("Tous")
            categories.addAll(noms)
        }

        if (loggedIn && role == Role.STUDENT) {
            registeredIds.clear()
            registeredIds.addAll(api.myRegistrations(auth).mapNotNull { it.id })
        }
        if (loggedIn && role == Role.ADMIN) {
            users.clear()
            users.addAll(api.users(auth).map { AppUser(it.id, it.name, it.email, roleOf(it.role), it.photo, it.phone) })
        }
    }

    // Visiteur : charge les événements validés (au démarrage / après déconnexion)
    fun refreshPublic() {
        if (!ApiConfig.USE_API) return
        scope.launch {
            try { refresh(); apiError = null } catch (e: Exception) { apiError = errorText(e) }
        }
    }

    // Connexion via l'API. Retourne null si OK, sinon le message d'erreur.
    suspend fun loginApi(email: String, password: String): String? = try {
        val r = api.login(LoginRequest(email.trim(), password))
        ApiClient.token = r.token
        userName = r.name; userEmail = r.email; role = roleOf(r.role); userPhoto = r.photo
        refresh()
        null
    } catch (e: Exception) { errorText(e) }

    suspend fun registerApi(name: String, email: String, password: String, phone: String): String? = try {
        val r = api.register(RegisterRequest(name.trim(), email.trim(), password, phone.trim()))
        ApiClient.token = r.token
        userName = r.name; userEmail = r.email; role = roleOf(r.role); userPhoto = r.photo
        refresh()
        null
    } catch (e: Exception) { errorText(e) }

    fun logout() {
        ApiClient.token = ""
        registeredIds.clear()
        userName = ""; userEmail = ""; role = Role.STUDENT; userPhoto = null
        refreshPublic()
    }

    // ---------- Mode fictif ----------
    // Noms saisis à l'inscription (email -> nom)
    private val knownNames = mutableMapOf<String, String>()

    fun signUp(name: String, email: String) {
        knownNames[email.trim().lowercase()] = name.trim()
    }

    // Le rôle est deviné d'après l'email (mode fictif uniquement)
    fun login(email: String) {
        val key = email.trim().lowercase()
        val local = key.substringBefore("@")
        userEmail = email.trim()
        userName = knownNames[key] ?: nameFromEmail(key)
        role = when {
            key in ADMIN_EMAILS -> Role.ADMIN
            local.contains("prof") || local.contains("enseignant") ||
                local.contains("teacher") || local.startsWith("pr.") -> Role.TEACHER
            else -> Role.STUDENT
        }
    }

    // Administrateurs (mode fictif ; avec le backend c'est application.properties)
    private val ADMIN_EMAILS = listOf("omarkochtane51@gmail.com", "yassinesouissi011@gmail.com")

    private fun nameFromEmail(email: String): String =
        email.substringBefore("@")
            .split(".", "_", "-")
            .filter { it.isNotBlank() }
            .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }

    // ---------- Événements ----------
    fun byId(id: Int): Event? = events.firstOrNull { it.id == id }

    // Visible par les visiteurs et les étudiants : uniquement les événements VALIDÉS
    val publicEvents: List<Event>
        get() = events.filter { it.status == EventStatus.VALIDE }

    // Événements de l'enseignant connecté (+ événements de démonstration en mode fictif)
    val teacherEvents: List<Event>
        get() = events.filter { it.owner == userEmail || it.owner == "demo" }

    fun nextId(): Int = (events.maxOfOrNull { it.id } ?: 0) + 1

    fun addEvent(event: Event) {
        if (ApiConfig.USE_API) {
            if (role == Role.ADMIN) call { api.createAdminEvent(ApiClient.bearer, event.toDto()) }
            else call { api.createEvent(ApiClient.bearer, event.toDto()) }
        }
        else events.add(0, event)
    }

    fun updateEvent(event: Event) {
        if (ApiConfig.USE_API) {
            // Le statut est géré par le serveur (REFUSÉ -> EN_ATTENTE après modification)
            call { api.updateEvent(ApiClient.bearer, event.id, event.toDto()) }
        } else {
            val index = events.indexOfFirst { it.id == event.id }
            if (index >= 0) events[index] = event
        }
    }

    fun deleteEvent(id: Int) {
        if (ApiConfig.USE_API) call { api.deleteEvent(ApiClient.bearer, id) }
        else { events.removeAll { it.id == id }; registeredIds.remove(id) }
    }

    // Admin : valider ou refuser
    fun setStatus(id: Int, status: EventStatus) {
        if (ApiConfig.USE_API) {
            call { api.setStatus(ApiClient.bearer, id, StatusRequest(status.name)) }
        } else {
            val e = byId(id) ?: return
            updateEvent(e.copy(status = status))
        }
    }

    // Photo de profil de l'utilisateur connecté
    fun setPhoto(base64: String) {
        userPhoto = base64
        if (ApiConfig.USE_API) call { api.updatePhoto(ApiClient.bearer, PhotoRequest(base64)) }
    }

    // Admin : choisir si un utilisateur est étudiant ou enseignant
    fun setRole(id: Int, newRole: Role) {
        if (ApiConfig.USE_API) {
            call { api.setRole(ApiClient.bearer, id, RoleRequest(newRole.name)) }
        } else {
            val i = users.indexOfFirst { it.id == id }
            if (i >= 0) users[i] = users[i].copy(role = newRole)
        }
    }

    fun deleteUser(id: Int) {
        if (ApiConfig.USE_API) call { api.deleteUser(ApiClient.bearer, id) }
        else users.removeAll { it.id == id }
    }

    // ---------- Participants (T5) ----------
    // Liste des étudiants inscrits à un événement de l'enseignant connecté.
    // Mode API : GET /api/teacher/events/{id}/participants (RG6 : seulement ses propres événements).
    // Mode fictif : noms de démonstration.
    fun loadParticipants(eventId: Int, onResult: (List<AppUser>) -> Unit) {
        if (!ApiConfig.USE_API) {
            val event = byId(eventId)
            val total = if (event == null) 0 else count(event)
            onResult(
                SampleData.participantNames.take(total)
                    .mapIndexed { i, nom -> AppUser(i + 1, nom, "", Role.STUDENT) }
            )
            return
        }
        scope.launch {
            try {
                // L'administrateur consulte les inscrits de tous les événements,
                // l'enseignant uniquement ceux des siens (RG6).
                val list = if (role == Role.ADMIN)
                    api.adminParticipants(ApiClient.bearer, eventId)
                else
                    api.participants(ApiClient.bearer, eventId)
                onResult(list.map { AppUser(it.id, it.name, it.email, roleOf(it.role), it.photo, it.phone) })
                apiError = null
            } catch (e: Exception) {
                apiError = errorText(e)
                onResult(emptyList())
            }
        }
    }

    // ---------- Inscriptions ----------
    fun isRegistered(id: Int) = registeredIds.contains(id)

    // RG8 : nombre d'inscrits affiché (en mode fictif on ajoute l'étudiant courant)
    fun count(event: Event) =
        if (ApiConfig.USE_API) event.registered
        else event.registered + if (isRegistered(event.id)) 1 else 0

    fun isFull(event: Event) = count(event) >= event.maxParticipants

    // Une seule inscription par événement, et pas si l'événement est complet
    fun register(event: Event): Boolean {
        if (isRegistered(event.id) || isFull(event)) return false
        registeredIds.add(event.id)
        if (ApiConfig.USE_API) call { api.registerToEvent(ApiClient.bearer, event.id) }
        return true
    }

    fun unregister(id: Int) {
        registeredIds.remove(id)
        if (ApiConfig.USE_API) call { api.unregister(ApiClient.bearer, id) }
    }
}

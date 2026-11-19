package tn.iset.smartevent.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// ---------- Configuration ----------
// USE_API = true quand le backend Spring Boot est lancé ; false pour la démo hors ligne.
//
// L'adresse du serveur n'est plus écrite en dur : l'utilisateur la saisit dans
// l'application (lien « Adresse du serveur » sur l'écran de connexion) et elle est
// mémorisée sur le téléphone. Le même APK fonctionne donc sur n'importe quel Wi-Fi.
object ApiConfig {
    const val USE_API = true

    // Valeur par défaut : adresse du PC lorsqu'il partage sa connexion
    // (point d'accès sans fil mobile de Windows). Elle ne change pas d'une fois
    // sur l'autre, donc les utilisateurs n'ont rien à saisir : il leur suffit de
    // se connecter au Wi-Fi partagé par le PC.
    //
    // Autres cas, à saisir dans l'écran « Adresse du serveur » :
    //   émulateur Android Studio ......... 10.0.2.2
    //   PC sur un Wi-Fi ordinaire ........ son adresse IPv4 (commande ipconfig)
    const val DEFAULT_BASE_URL = "http://192.168.137.1:8081/"

    // Adresse réellement utilisée par Retrofit.
    var baseUrl: String = DEFAULT_BASE_URL
        private set

    // Change l'adresse et force la reconstruction du client Retrofit.
    fun changerAdresse(url: String) {
        baseUrl = url
        ApiClient.reconstruire()
    }

    // Complète ce que l'utilisateur tape : "192.168.1.150" devient
    // "http://192.168.1.150:8081/". Accepte aussi une adresse complète.
    fun normaliser(saisie: String): String {
        var url = saisie.trim()
        if (url.isEmpty()) return DEFAULT_BASE_URL
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://$url"
        val hote = url.substringAfter("://").substringBefore("/")
        if (!hote.contains(":")) url = url.replaceFirst(hote, "$hote:8081")
        if (!url.endsWith("/")) url = "$url/"
        return url
    }
}

// ---------- DTO (objets échangés en JSON) ----------
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val phone: String)
// role : "STUDENT" | "TEACHER" | "ADMIN"
data class AuthResponse(
    val token: String, val id: Int, val name: String, val email: String, val role: String,
    val photo: String? = null
)

data class EventDto(
    val id: Int? = null,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String,
    val maxParticipants: Int,
    val registered: Int = 0,
    val status: String = "EN_ATTENTE",   // EN_ATTENTE | VALIDE | REFUSE
    val ownerEmail: String? = null,
    val image: String? = null            // affiche (JPEG en base64)
)

data class UserDto(val id: Int, val name: String, val email: String, val role: String, val photo: String? = null, val phone: String? = null)
data class CategoryDto(val id: Int, val name: String)
data class StatusRequest(val status: String)
data class RoleRequest(val role: String)
data class PhotoRequest(val photo: String?)

// ---------- Endpoints REST (le token JWT est envoyé dans l'en-tête Authorization) ----------
interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    // Événements VALIDÉS (visiteurs et étudiants)
    @GET("api/events")
    suspend fun publicEvents(): List<EventDto>

    @GET("api/events/{id}")
    suspend fun event(@Path("id") id: Int): EventDto

    // Catégories (entité Category du serveur)
    @GET("api/categories")
    suspend fun categories(): List<CategoryDto>

    // Inscription (une seule par événement, refusée si complet)
    @POST("api/events/{id}/register")
    suspend fun registerToEvent(@Header("Authorization") auth: String, @Path("id") id: Int)

    @DELETE("api/events/{id}/register")
    suspend fun unregister(@Header("Authorization") auth: String, @Path("id") id: Int)

    @GET("api/me/registrations")
    suspend fun myRegistrations(@Header("Authorization") auth: String): List<EventDto>

    // Enseignant
    @GET("api/teacher/events")
    suspend fun teacherEvents(@Header("Authorization") auth: String): List<EventDto>

    @POST("api/teacher/events")
    suspend fun createEvent(@Header("Authorization") auth: String, @Body body: EventDto): EventDto

    @PUT("api/teacher/events/{id}")
    suspend fun updateEvent(@Header("Authorization") auth: String, @Path("id") id: Int, @Body body: EventDto): EventDto

    @DELETE("api/teacher/events/{id}")
    suspend fun deleteEvent(@Header("Authorization") auth: String, @Path("id") id: Int)

    @GET("api/teacher/events/{id}/participants")
    suspend fun participants(@Header("Authorization") auth: String, @Path("id") id: Int): List<UserDto>

    // Même liste côté administrateur, pour n'importe quel événement
    @GET("api/admin/events/{id}/participants")
    suspend fun adminParticipants(@Header("Authorization") auth: String, @Path("id") id: Int): List<UserDto>

    // Profil (tous les rôles)
    @PUT("api/me/photo")
    suspend fun updatePhoto(@Header("Authorization") auth: String, @Body body: PhotoRequest)

    // Admin
    @PUT("api/admin/users/{id}/role")
    suspend fun setRole(@Header("Authorization") auth: String, @Path("id") id: Int, @Body body: RoleRequest)

    @GET("api/admin/users")
    suspend fun users(@Header("Authorization") auth: String): List<UserDto>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(@Header("Authorization") auth: String, @Path("id") id: Int)

    @POST("api/admin/events")
    suspend fun createAdminEvent(@Header("Authorization") auth: String, @Body body: EventDto): EventDto

    @GET("api/admin/events")
    suspend fun allEvents(@Header("Authorization") auth: String): List<EventDto>

    @PUT("api/admin/events/{id}/status")
    suspend fun setStatus(@Header("Authorization") auth: String, @Path("id") id: Int, @Body body: StatusRequest)
}

// Client Retrofit unique
object ApiClient {
    // Reconstruit à la demande : l'adresse du serveur peut changer en cours d'utilisation.
    @Volatile
    private var instance: ApiService? = null

    val service: ApiService
        get() = instance ?: creer().also { instance = it }

    private fun creer(): ApiService =
        Retrofit.Builder()
            .baseUrl(ApiConfig.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    // Appelé après un changement d'adresse : le prochain appel recrée le client.
    fun reconstruire() {
        instance = null
    }

    // Jeton JWT reçu au login
    var token: String = ""
    val bearer: String get() = "Bearer $token"
}

package tn.iset.smartevent.data

// Statuts d'un événement (cahier des charges : EN_ATTENTE / VALIDÉ / REFUSÉ)
enum class EventStatus(val label: String) {
    VALIDE("VALIDÉ"),
    EN_ATTENTE("EN ATTENTE"),
    REFUSE("REFUSÉ")
}

// Un événement universitaire (voir cahier des charges : entité Event)
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String,
    val maxParticipants: Int,
    val registered: Int,
    val status: EventStatus = EventStatus.VALIDE,
    val owner: String = "demo",   // email de l'enseignant ("demo" = données fictives)
    val image: String? = null      // affiche (JPEG en base64)
)

// Données fictives pour l'instant (plus tard : vraies données via Retrofit)
object SampleData {
    val categories = listOf("Tous", "Informatique", "Conférence", "Séminaire", "Sport", "Club")

    val events = listOf(
        Event(
            1, "Workshop Angular",
            "Découvrez les bases et les bonnes pratiques avec des cas concrets, animé par un enseignant de l'établissement.",
            "20/11/2026", "10:00", "Salle A101", "Informatique", 30, 23
        ),
        Event(
            2, "Conférence IA et Société",
            "Une conférence sur l'impact de l'intelligence artificielle sur la société et les métiers de demain.",
            "25/11/2026", "14:00", "Amphi B", "Conférence", 120, 87
        ),
        Event(
            3, "Séminaire Cybersécurité",
            "Sensibilisation aux risques informatiques et aux bonnes pratiques de sécurité.",
            "02/12/2026", "09:30", "Salle C204", "Séminaire", 40, 18
        ),
        Event(
            4, "Tournoi de Football",
            "Tournoi inter-classes organisé par le club sportif de l'institut.",
            "05/12/2026", "15:00", "Terrain de sport", "Sport", 60, 42
        ),
        Event(
            5, "Hackathon Mobile",
            "24 heures pour concevoir une application mobile par équipe de trois.",
            "12/12/2026", "08:00", "Salle B12", "Informatique", 50, 0,
            status = EventStatus.EN_ATTENTE
        ),
        Event(
            6, "Journée Portes Ouvertes",
            "Présentation des filières de l'institut aux futurs étudiants.",
            "15/12/2026", "09:00", "Hall principal", "Club", 200, 0,
            status = EventStatus.REFUSE
        )
    )

    // Noms fictifs pour la liste des participants
    val participantNames = listOf(
        "Amira Ben Salah", "Mohamed Trabelsi", "Sarra Jlassi", "Yassine Souissi",
        "Nour Gharbi", "Hedi Mansouri", "Ines Cherif", "Rami Ayari",
        "Khaoula Dridi", "Anis Bouzid", "Mariem Hamdi", "Walid Saidi"
    )
}

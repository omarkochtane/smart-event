package tn.iset.smartevent.data

import android.content.Context
import tn.iset.smartevent.data.api.ApiConfig

// Mémorise l'adresse du serveur sur le téléphone (SharedPreferences).
// Ainsi le même APK fonctionne sur n'importe quel réseau : l'utilisateur saisit
// l'adresse du PC une seule fois, elle est retrouvée aux démarrages suivants.
object ServerSettings {
    private const val FICHIER = "smartevent_prefs"
    private const val CLE_URL = "server_url"

    private fun prefs(context: Context) =
        context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE)

    // Appelé au démarrage de l'application (MainActivity).
    fun charger(context: Context) {
        val url = prefs(context).getString(CLE_URL, null)
        if (!url.isNullOrBlank()) ApiConfig.changerAdresse(url)
    }

    // Adresse affichée dans le champ de saisie.
    fun adresseActuelle(context: Context): String =
        prefs(context).getString(CLE_URL, null) ?: ApiConfig.baseUrl

    // Enregistre la saisie de l'utilisateur et l'applique immédiatement.
    fun enregistrer(context: Context, saisie: String) {
        val url = ApiConfig.normaliser(saisie)
        prefs(context).edit().putString(CLE_URL, url).apply()
        ApiConfig.changerAdresse(url)
    }

    // Revient à l'adresse par défaut (émulateur).
    fun reinitialiser(context: Context) {
        prefs(context).edit().remove(CLE_URL).apply()
        ApiConfig.changerAdresse(ApiConfig.DEFAULT_BASE_URL)
    }
}

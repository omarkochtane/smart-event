package tn.iset.smartevent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tn.iset.smartevent.data.ServerSettings
import tn.iset.smartevent.navigation.AppNavigation
import tn.iset.smartevent.ui.theme.SmartEventTheme

// Activité unique de l'application : applique le thème puis affiche la navigation.
// Tous les écrans sont des Composables affichés dans cette seule activité.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Adresse du serveur mémorisée lors d'une précédente utilisation
        ServerSettings.charger(this)
        enableEdgeToEdge()
        setContent {
            SmartEventTheme {
                AppNavigation()
            }
        }
    }
}

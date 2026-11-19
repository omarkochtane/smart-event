package tn.iset.smartevent.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.ServerSettings
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500

// Boîte de dialogue « Adresse du serveur ».
// Permet d'utiliser le même APK sur n'importe quel réseau : l'utilisateur saisit
// l'adresse du PC qui exécute le backend (visible avec ipconfig sur ce PC),
// par exemple 192.168.1.150. Le port 8081 est ajouté automatiquement.
@Composable
fun ServerDialog(onClose: () -> Unit) {
    val context = LocalContext.current
    var adresse by remember { mutableStateOf(ServerSettings.adresseActuelle(context)) }
    var message by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Adresse du serveur", fontWeight = FontWeight.Bold, color = Navy900) },
        text = {
            Column {
                Text(
                    "Saisir l'adresse du PC qui exécute le backend. " +
                        "Le téléphone et le PC doivent être sur le même Wi-Fi.",
                    fontSize = 13.sp, color = Slate500
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = adresse,
                    onValueChange = { adresse = it; message = null },
                    label = "Adresse",
                    placeholder = "192.168.1.150",
                    icon = Icons.Filled.Settings,
                    modifier = Modifier.fillMaxWidth()
                )
                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(message!!, fontSize = 12.sp, color = Slate500)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                ServerSettings.enregistrer(context, adresse)
                message = "Enregistré : " + ServerSettings.adresseActuelle(context)
                onClose()
            }) { Text("Enregistrer", color = Navy900, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = {
                ServerSettings.reinitialiser(context)
                adresse = ServerSettings.adresseActuelle(context)
                message = "Adresse par défaut rétablie"
            }) { Text("Par défaut", color = Slate500) }
        }
    )
}

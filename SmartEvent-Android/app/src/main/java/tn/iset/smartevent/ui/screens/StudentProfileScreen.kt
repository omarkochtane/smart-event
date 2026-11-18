package tn.iset.smartevent.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import tn.iset.smartevent.ui.components.Avatar
import tn.iset.smartevent.ui.components.uriToBase64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.InfoTile
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

// E9 : profil de l'étudiant (nom, email, photo) et déconnexion.
// La photo est choisie dans la galerie puis envoyée au serveur en base64.
@Composable
fun StudentProfileScreen(onLogout: () -> Unit, role: String = "STUDENT") {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Photo de profil : réduite à 400 px puis envoyée au serveur en base64
        if (uri != null) uriToBase64(context, uri, 400)?.let { AppState.setPhoto(it) }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Profil",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Avatar(AppState.userName, AppState.userPhoto, 96.dp)
        TextButton(onClick = { picker.launch("image/*") }) {
            Text(
                if (AppState.userPhoto == null) "Ajouter une photo" else "Changer la photo",
                color = Navy900, fontWeight = FontWeight.SemiBold
            )
        }
        Text(AppState.userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy900)
        Text(AppState.userEmail, fontSize = 13.sp, color = Slate500)

        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoTile("Rôle", role, Modifier.fillMaxWidth())
                if (role == "STUDENT") {
                    InfoTile(
                        "Inscriptions",
                        "${AppState.registeredIds.size} événement(s)",
                        Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Se déconnecter", color = Navy900, fontWeight = FontWeight.SemiBold)
        }
    }
}

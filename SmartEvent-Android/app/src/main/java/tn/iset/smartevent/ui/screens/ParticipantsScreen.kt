package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.data.AppUser
import tn.iset.smartevent.ui.components.Avatar
import tn.iset.smartevent.ui.components.BackButton
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

// T5 : liste des étudiants inscrits à un événement de l'enseignant (chargée depuis l'API)
@Composable
fun ParticipantsScreen(eventId: Int, onBack: () -> Unit) {
    val event = AppState.byId(eventId)
    var participants by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var charge by remember { mutableStateOf(false) }

    // Chargement au premier affichage de l'écran
    LaunchedEffect(eventId) {
        AppState.loadParticipants(eventId) { liste ->
            participants = liste
            charge = true
        }
    }

    val total = participants.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBack)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Participants", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy900)
                if (event != null) {
                    Text(
                        "${event.title} · $total / ${event.maxParticipants}",
                        fontSize = 12.sp, color = Slate500
                    )
                }
            }
        }

        if (charge && total == 0) {
            Text(
                "Aucun inscrit pour le moment.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(participants) { _, etudiant ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Avatar(etudiant.name, etudiant.photo, 36.dp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                etudiant.name, fontSize = 14.sp,
                                color = Navy900, fontWeight = FontWeight.Medium
                            )
                            if (etudiant.email.isNotBlank()) {
                                Text(etudiant.email, fontSize = 12.sp, color = Slate500)
                            }
                        }
                    }
                }
            }
        }
    }
}

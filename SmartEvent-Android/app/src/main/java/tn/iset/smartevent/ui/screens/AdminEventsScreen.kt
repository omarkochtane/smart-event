package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import tn.iset.smartevent.ui.components.Base64Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import tn.iset.smartevent.data.EventStatus
import tn.iset.smartevent.ui.components.CategoryChip
import tn.iset.smartevent.ui.components.StatusChip
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

// A4 / A5 / A6 : tous les événements (les trois statuts), ajout, validation et refus.
@Composable
fun AdminEventsScreen(onAdd: () -> Unit = {}, onParticipants: (Int) -> Unit = {}) {
    // Filtre par statut : par défaut les événements en attente de validation
    var filter by remember { mutableStateOf<EventStatus?>(EventStatus.EN_ATTENTE) }
    val list = AppState.events.filter { filter == null || it.status == filter }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Text(
            "Événements",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 8.dp)
        )
        Button(
            onClick = onAdd,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)
        ) { Text("+ Ajouter un événement", color = White, fontSize = 14.sp) }
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { CategoryChip("Tous", filter == null, onClick = { filter = null }) }
            items(EventStatus.values().toList()) { st ->
                CategoryChip(st.label, filter == st, onClick = { filter = st })
            }
        }

        if (list.isEmpty()) {
            Text(
                "Aucun événement dans cette catégorie.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(list) { event ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (event.image != null) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            ) { Base64Image(event.image, Modifier.fillMaxSize()) }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(event.category, fontSize = 12.sp, color = Slate500)
                            StatusChip(event.status)
                        }
                        Text(event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Text(
                            "${event.date} · ${event.time} · ${event.location}",
                            fontSize = 12.sp, color = Slate500
                        )
                        Text("Organisateur : ${event.owner}", fontSize = 12.sp, color = Slate500)
                        Text(event.description, fontSize = 13.sp, color = Navy900)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // A6 : l'administrateur valide ou refuse (RG5 : EN_ATTENTE, VALIDE, REFUSE)
                            if (event.status != EventStatus.REFUSE) {
                                OutlinedButton(
                                    onClick = { AppState.setStatus(event.id, EventStatus.REFUSE) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) { Text("Refuser", color = Navy900, fontSize = 13.sp) }
                            }
                            if (event.status != EventStatus.VALIDE) {
                                Button(
                                    onClick = { AppState.setStatus(event.id, EventStatus.VALIDE) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                                    modifier = Modifier.weight(1f)
                                ) { Text("Valider", color = White, fontSize = 13.sp) }
                            }
                        }

                        // L'administrateur consulte les inscrits de n'importe quel événement
                        OutlinedButton(
                            onClick = { onParticipants(event.id) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Participants (${event.registered})",
                                color = Navy900, fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

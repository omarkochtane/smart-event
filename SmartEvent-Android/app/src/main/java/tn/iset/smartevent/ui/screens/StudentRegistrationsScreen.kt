package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.EventCard
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.StatusGreen
import tn.iset.smartevent.ui.theme.StatusGreenBg

@Composable
fun StudentRegistrationsScreen(onEventClick: (Int) -> Unit) {
    val myEvents = AppState.publicEvents.filter { AppState.isRegistered(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Text(
            "Mes inscriptions",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.padding(20.dp)
        )

        if (myEvents.isEmpty()) {
            Text(
                "Vous n'êtes inscrit à aucun événement pour le moment.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(myEvents) { event ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    EventCard(event, onClick = { onEventClick(event.id) })
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StatusGreenBg, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                    ) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "✓ Inscription confirmée",
                                color = StatusGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                            )
                            TextButton(onClick = { AppState.unregister(event.id) }) {
                                Text("Annuler", color = Navy900, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

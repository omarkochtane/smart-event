package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import tn.iset.smartevent.ui.theme.Slate300
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

@Composable
fun StudentHomeScreen(onEventClick: (Int) -> Unit, onSearch: () -> Unit, onSeeRegs: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Bonjour,", fontSize = 14.sp, color = Slate500)
            Text(
                AppState.userName,
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900
            )
        }

        // Carte résumé
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "${AppState.registeredIds.size} inscription(s)",
                    color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text("Retrouvez vos événements à venir.", color = Cloud, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSeeRegs,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate300, contentColor = Navy900)
                    ) { Text("Mes inscriptions", fontSize = 13.sp) }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Prochains événements", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy900)
            TextButton(onClick = onSearch) { Text("Voir tout", color = Slate500, fontSize = 13.sp) }
        }

        AppState.publicEvents.take(3).forEach { event ->
            EventCard(event, onClick = { onEventClick(event.id) })
        }
    }
}

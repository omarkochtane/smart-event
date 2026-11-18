package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.TeacherEventCard
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500

// T1 / T3 / T4 : liste des événements de l'enseignant connecté, avec leur statut,
// et accès à la modification, à la suppression et aux participants (RG6).
@Composable
fun TeacherEventsScreen(onEdit: (Int) -> Unit, onParticipants: (Int) -> Unit) {
    // RG6 : uniquement les événements de l'enseignant connecté
    val mine = AppState.teacherEvents

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Text(
            "Mes événements",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.padding(20.dp)
        )
        if (mine.isEmpty()) {
            Text(
                "Vous n'avez créé aucun événement.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(mine) { event ->
                TeacherEventCard(
                    event,
                    onEdit = { onEdit(event.id) },
                    onParticipants = { onParticipants(event.id) }
                )
            }
        }
    }
}

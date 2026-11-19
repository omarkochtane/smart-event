package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.CategoryChip
import tn.iset.smartevent.ui.components.TeacherEventCard
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500

// T1 / T3 / T4 : événements de l'enseignant connecté, avec leur statut et l'accès
// à la modification, à la suppression et aux participants (RG6).
//
// Le filtre « Tous » affiche en plus les événements validés des autres enseignants :
// RG6 interdit de les modifier ou d'en consulter les inscrits, pas de les consulter.
@Composable
fun TeacherEventsScreen(onEdit: (Int) -> Unit, onParticipants: (Int) -> Unit) {
    var tous by remember { mutableStateOf(false) }

    // Les événements des autres enseignants sont chargés à la demande
    LaunchedEffect(tous) {
        if (tous) AppState.loadAutresEvenements()
    }

    // RG6 : par défaut, uniquement les événements de l'enseignant connecté
    val mine = AppState.teacherEvents
    val autres = AppState.autresEvenements.filter { it.owner != AppState.userEmail }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Text(
            if (tous) "Tous les événements" else "Mes événements",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.padding(20.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip("Mes événements", !tous, { tous = false })
            CategoryChip("Tous", tous, { tous = true })
        }

        if (!tous && mine.isEmpty()) {
            Text(
                "Vous n'avez créé aucun événement.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
        if (tous && autres.isEmpty()) {
            Text(
                "Aucun autre événement validé pour le moment.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!tous) {
                items(mine) { event ->
                    TeacherEventCard(
                        event,
                        onEdit = { onEdit(event.id) },
                        onParticipants = { onParticipants(event.id) }
                    )
                }
            } else {
                // Événements des autres enseignants : consultation seule (RG6)
                items(autres) { event ->
                    TeacherEventCard(
                        event,
                        onEdit = {},
                        onParticipants = {},
                        lectureSeule = true
                    )
                }
            }
        }
    }
}

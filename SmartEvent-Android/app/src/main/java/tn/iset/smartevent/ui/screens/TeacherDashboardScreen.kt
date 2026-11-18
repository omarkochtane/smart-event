package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.data.EventStatus
import tn.iset.smartevent.ui.components.InfoTile
import tn.iset.smartevent.ui.components.PrimaryButton
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500

@Composable
fun TeacherDashboardScreen(onAdd: () -> Unit, onSeeEvents: () -> Unit) {
    val mine = AppState.teacherEvents
    val validated = mine.count { it.status == EventStatus.VALIDE }
    val pending = mine.count { it.status == EventStatus.EN_ATTENTE }
    val refused = mine.count { it.status == EventStatus.REFUSE }
    val totalRegistered = mine.sumOf { it.registered }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text("Bonjour,", fontSize = 14.sp, color = Slate500)
            Text(AppState.userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900)
        }
        Text("Tableau de bord", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy900)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoTile("Événements", "${mine.size}", Modifier.weight(1f))
            InfoTile("Inscrits (total)", "$totalRegistered", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoTile("Validés", "$validated", Modifier.weight(1f))
            InfoTile("En attente", "$pending", Modifier.weight(1f))
            InfoTile("Refusés", "$refused", Modifier.weight(1f))
        }

        PrimaryButton("Ajouter un événement", onAdd)
        PrimaryButton("Voir mes événements", onSeeEvents)
    }
}

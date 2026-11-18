package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.BackButton
import tn.iset.smartevent.ui.components.Base64Image
import tn.iset.smartevent.ui.components.InfoTile
import tn.iset.smartevent.ui.components.PrimaryButton
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy700
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.StatusGreen
import tn.iset.smartevent.ui.theme.StatusGreenBg
import tn.iset.smartevent.ui.theme.White

// E5 / E6 / E7 : détails d'un événement et bouton d'inscription.
// L'inscription est refusée si l'événement est complet (RG2) ou déjà réservé (RG3).
@Composable
fun EventDetailScreen(
    eventId: Int,
    onBack: () -> Unit,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    actionEnabled: Boolean = true,
    message: String? = null
) {
    val event = AppState.byId(eventId)
    if (event == null) {
        Box(Modifier.fillMaxSize().background(Cloud).statusBarsPadding().padding(20.dp)) {
            BackButton(onBack)
        }
        return
    }

    val count = AppState.count(event)

    Column(modifier.fillMaxSize().background(Cloud)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // En-tête image (dégradé) + bouton retour
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Brush.linearGradient(listOf(Slate500, Navy700)))
            ) {
                Base64Image(event.image, Modifier.fillMaxSize())
                Box(Modifier.statusBarsPadding().padding(16.dp)) {
                    BackButton(onBack)
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(event.category, fontSize = 12.sp, color = Slate500)
                Text(event.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy900)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoTile("Date", event.date, Modifier.weight(1f))
                    InfoTile("Heure", event.time, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoTile("Lieu", event.location, Modifier.weight(1f))
                    InfoTile("Catégorie", event.category, Modifier.weight(1f))
                }

                // RG9 : message de confirmation
                if (message != null) {
                    Text(
                        "\u2713 " + message,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StatusGreenBg, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    )
                }

                // Participants (RG8)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Participants", fontSize = 13.sp, color = Navy900)
                        Text(
                            "$count / ${event.maxParticipants} participants",
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Navy900
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { count.toFloat() / event.maxParticipants },
                        color = Navy900,
                        trackColor = Cloud,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(event.description, fontSize = 13.sp, color = Slate500)
            }
        }

        // Bouton du bas : un visiteur doit se connecter pour s'inscrire
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            PrimaryButton(actionText, onAction, enabled = actionEnabled)
        }
    }
}

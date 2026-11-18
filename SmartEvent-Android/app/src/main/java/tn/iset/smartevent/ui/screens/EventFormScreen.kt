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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import tn.iset.smartevent.ui.components.Base64Image
import tn.iset.smartevent.ui.components.uriToBase64
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.data.Event
import tn.iset.smartevent.data.EventStatus
import tn.iset.smartevent.ui.components.AppTextField
import tn.iset.smartevent.ui.components.BackButton
import tn.iset.smartevent.ui.components.CategoryChip
import tn.iset.smartevent.ui.components.PrimaryButton
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.StatusRed

// Formulaire pour AJOUTER (eventId = null) ou MODIFIER un événement
@Composable
fun EventFormScreen(eventId: Int?, onBack: (() -> Unit)?, onDone: () -> Unit) {
    val existing = eventId?.let { AppState.byId(it) }

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var date by remember { mutableStateOf(existing?.date ?: "") }
    var time by remember { mutableStateOf(existing?.time ?: "10:00") }
    var location by remember { mutableStateOf(existing?.location ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "Informatique") }
    var maxText by remember { mutableStateOf(existing?.maxParticipants?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var askDelete by remember { mutableStateOf(false) }
    var image by remember { mutableStateOf(existing?.image) }
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // L'affiche choisie dans la galerie est réduite puis convertie en base64
            val b64 = uriToBase64(context, uri)
            if (b64 != null) image = b64 else error = "Image illisible, choisissez-en une autre."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                BackButton(onBack)
                Spacer(Modifier.width(12.dp))
            }
            Text(
                if (existing == null) "Ajouter un événement" else "Modifier l'événement",
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy900
            )
        }

        if (existing == null) {
            Text(
                "Un nouvel événement est envoyé à l'administrateur pour validation (statut EN ATTENTE).",
                fontSize = 12.sp, color = Slate500
            )
        }

        // Affiche de l'événement
        Column {
            Text("Affiche", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Navy900)
            Spacer(Modifier.height(6.dp))
            if (image != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) { Base64Image(image, Modifier.fillMaxSize()) }
                Spacer(Modifier.height(6.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { picker.launch("image/*") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) { Text(if (image == null) "Choisir une image" else "Changer l'image", color = Navy900, fontSize = 13.sp) }
                if (image != null) {
                    OutlinedButton(
                        onClick = { image = null },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Retirer", color = Navy900, fontSize = 13.sp) }
                }
            }
        }

        AppTextField(title, { title = it }, "Titre", "Workshop Angular", null)
        AppTextField(
            description, { description = it }, "Description", "Décrivez l'événement...",
            null, minLines = 3
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AppTextField(date, { date = it }, "Date", "20/11/2026", null, Modifier.weight(1f))
            AppTextField(time, { time = it }, "Heure", "10:00", null, Modifier.weight(1f))
        }
        AppTextField(location, { location = it }, "Lieu", "Salle A101", null)

        Column {
            Text("Catégorie", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Navy900)
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AppState.categories.filter { it != "Tous" }) { cat ->
                    CategoryChip(cat, category == cat, onClick = { category = cat })
                }
            }
        }

        AppTextField(
            maxText, { maxText = it.filter { c -> c.isDigit() } },
            "Nombre maximum de participants", "30", null,
            keyboardType = KeyboardType.Number
        )

        if (error != null) {
            Text(error!!, color = StatusRed, fontSize = 13.sp)
        }

        PrimaryButton(if (existing == null) "Envoyer pour validation" else "Enregistrer", {
            val max = maxText.toIntOrNull()
            when {
                title.isBlank() || date.isBlank() || location.isBlank() ->
                    // Contrôles de saisie avant l'enregistrement
                    error = "Le titre, la date et le lieu sont obligatoires."
                max == null || max <= 0 ->
                    error = "Le nombre de participants doit être supérieur à 0."
                existing != null && max < existing.registered ->
                    error = "Le maximum ne peut pas être inférieur au nombre d'inscrits (${existing.registered})."
                existing == null -> {
                    // RG4 : un événement créé par un enseignant part EN_ATTENTE ;
                    // créé par l'administrateur, il est directement VALIDÉ
                    AppState.addEvent(
                        Event(
                            id = AppState.nextId(),
                            title = title.trim(),
                            description = description.trim(),
                            date = date.trim(),
                            time = time.trim(),
                            location = location.trim(),
                            category = category,
                            maxParticipants = max,
                            registered = 0,
                            image = image,
                            status = if (AppState.role == tn.iset.smartevent.data.Role.ADMIN) EventStatus.VALIDE else EventStatus.EN_ATTENTE,
                            owner = AppState.userEmail
                        )
                    )
                    onDone()
                }
                else -> {
                    // Un événement refusé repart en validation après modification
                    AppState.updateEvent(
                        existing.copy(
                            title = title.trim(),
                            description = description.trim(),
                            date = date.trim(),
                            time = time.trim(),
                            location = location.trim(),
                            category = category,
                            maxParticipants = max,
                            image = image,
                            // un événement refusé, une fois modifié, repart en validation
                            status = if (existing.status == EventStatus.REFUSE)
                                EventStatus.EN_ATTENTE else existing.status
                        )
                    )
                    onDone()
                }
            }
        })

        if (existing != null) {
            TextButton(
                onClick = { askDelete = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Supprimer l'événement", color = Navy900, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (askDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { askDelete = false },
            title = { Text("Supprimer l'événement ?") },
            text = { Text("« ${existing.title} » sera supprimé définitivement.") },
            confirmButton = {
                TextButton(onClick = {
                    askDelete = false
                    AppState.deleteEvent(existing.id)
                    onDone()
                }) { Text("Supprimer", color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { askDelete = false }) { Text("Annuler", color = Navy900) }
            }
        )
    }
}

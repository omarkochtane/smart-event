package tn.iset.smartevent.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import tn.iset.smartevent.data.EventStatus
import tn.iset.smartevent.ui.theme.StatusAmber
import tn.iset.smartevent.ui.theme.StatusAmberBg
import tn.iset.smartevent.ui.theme.StatusGreen
import tn.iset.smartevent.ui.theme.StatusGreenBg
import tn.iset.smartevent.ui.theme.StatusRed
import tn.iset.smartevent.ui.theme.StatusRedBg
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.data.Event
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy700
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate300
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

// Champ de saisie avec label au-dessus et icône (facultative) à gauche
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    val leading: (@Composable () -> Unit)? =
        if (icon != null) { { Icon(icon, contentDescription = null, tint = Slate500) } } else null

    Column(modifier) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Navy900)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Slate300) },
            leadingIcon = leading,
            singleLine = minLines == 1,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = Navy900,
                unfocusedBorderColor = Slate300,
                focusedTextColor = Navy900,
                unfocusedTextColor = Navy900
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Bouton principal (fond bleu nuit, texte blanc)
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Navy900,
            contentColor = White,
            disabledContainerColor = Slate300,
            disabledContentColor = White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

// Petit bouton retour rond
@Composable
fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Navy900)
    }
}

// Chip de catégorie (sélectionnée = fond bleu nuit)
@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit, compact: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Navy900 else White)
            .clickable { onClick() }
            .padding(horizontal = if (compact) 12.dp else 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            maxLines = 1,
            softWrap = false,
            color = if (selected) White else Navy900,
            fontSize = if (compact) 12.sp else 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Carte d'un événement (liste et accueil)
@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Image fictive (dégradé)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Brush.linearGradient(listOf(Slate500, Navy700)))
        ) {
            Base64Image(event.image, Modifier.matchParentSize())
        }
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Cloud)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(event.category, fontSize = 11.sp, color = Slate500)
                }
                // RG8 : affichage "23 / 30 participants"
                Text(
                    "${AppState.count(event)} / ${event.maxParticipants} participants",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Navy900
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, null, tint = Slate500, modifier = Modifier.size(14.dp))
                Spacer(Modifier.size(4.dp))
                Text(
                    "${event.date} · ${event.time} · ${event.location}",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }
        }
    }
}

@Composable
fun InfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Cloud),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = Slate500)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
        }
    }
}

// Pastille de statut : VALIDÉ (vert), EN ATTENTE (orange), REFUSÉ (rouge)
@Composable
fun StatusChip(status: EventStatus) {
    val (textColor, bg) = when (status) {
        EventStatus.VALIDE -> StatusGreen to StatusGreenBg
        EventStatus.EN_ATTENTE -> StatusAmber to StatusAmberBg
        EventStatus.REFUSE -> StatusRed to StatusRedBg
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

// Carte d'un événement côté enseignant (avec statut et actions)
@Composable
fun TeacherEventCard(
    event: Event,
    onEdit: () -> Unit,
    onParticipants: () -> Unit,
    // lectureSeule : événement d'un autre enseignant. RG6 : consultation autorisée,
    // mais ni modification ni accès aux participants.
    lectureSeule: Boolean = false
) {
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
                        .height(110.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) { Base64Image(event.image, Modifier.matchParentSize()) }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(event.category, fontSize = 12.sp, color = Slate500)
                StatusChip(event.status)
            }
            Text(event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
            Text("${event.date} · ${event.time} · ${event.location}", fontSize = 12.sp, color = Slate500)
            Text(
                "${AppState.count(event)} / ${event.maxParticipants} participants",
                fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Navy900
            )
            if (lectureSeule) {
                Text("Organisateur : ${event.owner}", fontSize = 12.sp, color = Slate500)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onParticipants,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Participants", color = Navy900, fontSize = 13.sp) }
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Modifier", color = Navy900, fontSize = 13.sp) }
                }
            }
        }
    }
}

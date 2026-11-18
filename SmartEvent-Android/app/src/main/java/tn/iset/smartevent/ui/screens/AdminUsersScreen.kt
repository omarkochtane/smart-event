package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import tn.iset.smartevent.ui.components.Avatar
import tn.iset.smartevent.ui.components.CategoryChip
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.data.AppUser
import tn.iset.smartevent.data.Role
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.StatusRed
import tn.iset.smartevent.ui.theme.White

@Composable
fun AdminUsersScreen() {
    var toDelete by remember { mutableStateOf<AppUser?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Text(
            "Utilisateurs",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.padding(20.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(AppState.users.toList()) { user ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(user.name, user.photo, 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            Text(user.email, fontSize = 12.sp, color = Slate500)
                            if (!user.phone.isNullOrBlank()) Text(user.phone, fontSize = 12.sp, color = Slate500)
                            Text(
                                when (user.role) {
                                    Role.STUDENT -> "ÉTUDIANT"
                                    Role.TEACHER -> "ENSEIGNANT"
                                    Role.ADMIN -> "ADMIN"
                                },
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate500
                            )
                            // L'administrateur choisit : étudiant ou enseignant
                            if (user.role != Role.ADMIN) {
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CategoryChip("Étudiant", user.role == Role.STUDENT,
                                        onClick = { AppState.setRole(user.id, Role.STUDENT) }, compact = true)
                                    CategoryChip("Enseignant", user.role == Role.TEACHER,
                                        onClick = { AppState.setRole(user.id, Role.TEACHER) }, compact = true)
                                }
                            }
                        }
                        // Pas de suppression de l'administrateur
                        if (user.role != Role.ADMIN) {
                            OutlinedButton(
                                onClick = { toDelete = user },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Navy900)
                            }
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Supprimer l'utilisateur ?") },
            text = { Text("${user.name} sera supprimé définitivement.") },
            confirmButton = {
                TextButton(onClick = {
                    AppState.deleteUser(user.id)
                    toDelete = null
                }) { Text("Supprimer", color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("Annuler", color = Navy900) }
            }
        )
    }
}

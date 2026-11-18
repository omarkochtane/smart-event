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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import tn.iset.smartevent.ui.components.EventCard
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate300
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

@Composable
fun StudentSearchScreen(onEventClick: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf("Tous") }

    // Filtre par catégorie + recherche dans le titre
    val results = AppState.publicEvents.filter {
        (selected == "Tous" || it.category == selected) &&
            it.title.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .statusBarsPadding()
    ) {
        Text(
            "Recherche",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900,
            modifier = Modifier.padding(20.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Rechercher un événement...", color = Slate300) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Slate500) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = Navy900,
                unfocusedBorderColor = White,
                focusedTextColor = Navy900,
                unfocusedTextColor = Navy900
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppState.categories) { cat ->
                CategoryChip(cat, selected == cat, onClick = { selected = cat })
            }
        }
        if (results.isEmpty()) {
            Text(
                "Aucun événement trouvé.",
                color = Slate500, fontSize = 14.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(results) { event ->
                EventCard(event, onClick = { onEventClick(event.id) })
            }
        }
    }
}

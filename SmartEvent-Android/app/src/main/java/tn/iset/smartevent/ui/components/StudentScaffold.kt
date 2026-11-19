package tn.iset.smartevent.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import tn.iset.smartevent.navigation.Routes
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

// Barre de navigation du bas de l'espace étudiant (Accueil, Recherche, Inscriptions, Profil).
class TabItem(val route: String, val label: String, val icon: ImageVector)

private val studentTabs = listOf(
    TabItem(Routes.STUDENT_HOME, "Accueil", Icons.Default.Home),
    TabItem(Routes.STUDENT_SEARCH, "Recherche", Icons.Default.Search),
    TabItem(Routes.STUDENT_REGS, "Inscriptions", Icons.Default.CheckCircle),
    TabItem(Routes.STUDENT_PROFILE, "Profil", Icons.Default.Person)
)

private val teacherTabs = listOf(
    TabItem(Routes.TEACHER_HOME, "Dashboard", Icons.Default.Home),
    TabItem(Routes.TEACHER_EVENTS, "Événements", Icons.AutoMirrored.Filled.List),
    TabItem(Routes.TEACHER_ADD, "Ajouter", Icons.Default.Add),
    TabItem(Routes.TEACHER_PROFILE, "Profil", Icons.Default.Person)
)

private val adminTabs = listOf(
    TabItem(Routes.ADMIN_HOME, "Dashboard", Icons.Default.Home),
    TabItem(Routes.ADMIN_USERS, "Utilisateurs", Icons.Default.Person),
    TabItem(Routes.ADMIN_EVENTS, "Événements", Icons.AutoMirrored.Filled.List),
    TabItem(Routes.ADMIN_PROFILE, "Profil", Icons.Default.AccountCircle)
)

// Structure commune : contenu + barre de navigation en bas
@Composable
fun BottomTabsScaffold(
    tabs: List<TabItem>,
    selected: String,
    onSelect: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = Cloud,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // navigationBarsPadding : la barre d'onglets se place au-dessus
            // de la barre système d'Android (boutons retour / accueil).
            NavigationBar(
                containerColor = White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab.route,
                        onClick = { onSelect(tab.route) },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = Navy900,
                            indicatorColor = Navy900,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}

@Composable
fun StudentScaffold(selected: String, onSelect: (String) -> Unit, content: @Composable () -> Unit) {
    BottomTabsScaffold(studentTabs, selected, onSelect, content)
}

@Composable
fun TeacherScaffold(selected: String, onSelect: (String) -> Unit, content: @Composable () -> Unit) {
    BottomTabsScaffold(teacherTabs, selected, onSelect, content)
}

@Composable
fun AdminScaffold(selected: String, onSelect: (String) -> Unit, content: @Composable () -> Unit) {
    BottomTabsScaffold(adminTabs, selected, onSelect, content)
}

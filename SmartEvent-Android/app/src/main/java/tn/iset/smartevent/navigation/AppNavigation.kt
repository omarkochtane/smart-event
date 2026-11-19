package tn.iset.smartevent.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.data.Role
import tn.iset.smartevent.ui.components.StudentScaffold
import tn.iset.smartevent.ui.components.TeacherScaffold
import tn.iset.smartevent.ui.components.AdminScaffold
import tn.iset.smartevent.ui.screens.EventDetailScreen
import tn.iset.smartevent.ui.screens.EventFormScreen
import tn.iset.smartevent.ui.screens.AdminDashboardScreen
import tn.iset.smartevent.ui.screens.AdminEventsScreen
import tn.iset.smartevent.ui.screens.AdminUsersScreen
import tn.iset.smartevent.ui.screens.ParticipantsScreen
import tn.iset.smartevent.ui.screens.TeacherDashboardScreen
import tn.iset.smartevent.ui.screens.TeacherEventsScreen
import tn.iset.smartevent.ui.screens.EventListScreen
import tn.iset.smartevent.ui.screens.HomeScreen
import tn.iset.smartevent.ui.screens.LoginScreen
import tn.iset.smartevent.ui.screens.RegisterScreen
import tn.iset.smartevent.ui.screens.SplashScreen
import tn.iset.smartevent.ui.screens.StudentHomeScreen
import tn.iset.smartevent.ui.screens.StudentProfileScreen
import tn.iset.smartevent.ui.screens.StudentRegistrationsScreen
import tn.iset.smartevent.ui.screens.StudentSearchScreen

// Noms des écrans
object Routes {
    // Public
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val EVENTS = "events"
    const val EVENT_DETAIL = "event/{id}"
    fun eventDetail(id: Int) = "event/$id"

    // Étudiant
    const val STUDENT_HOME = "student_home"
    const val STUDENT_SEARCH = "student_search"
    const val STUDENT_REGS = "student_regs"
    const val STUDENT_PROFILE = "student_profile"
    const val STUDENT_EVENT = "student_event/{id}"
    fun studentEvent(id: Int) = "student_event/$id"

    // Enseignant
    const val TEACHER_HOME = "teacher_home"
    const val TEACHER_EVENTS = "teacher_events"
    const val TEACHER_ADD = "teacher_add"
    const val TEACHER_PROFILE = "teacher_profile"
    const val TEACHER_EDIT = "teacher_edit/{id}"
    const val TEACHER_PARTICIPANTS = "teacher_participants/{id}"
    fun teacherEdit(id: Int) = "teacher_edit/$id"
    fun teacherParticipants(id: Int) = "teacher_participants/$id"

    // Admin
    const val ADMIN_HOME = "admin_home"
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_EVENTS = "admin_events"
    const val ADMIN_PROFILE = "admin_profile"
    const val ADMIN_ADD = "admin_add"
    const val ADMIN_PARTICIPANTS = "admin_participants/{id}"
    fun adminParticipants(id: Int) = "admin_participants/$id"
}

// Déconnexion : retour à l'accueil public en vidant la pile de l'espace connecté.
// "racine" est l'écran d'accueil du rôle (étudiant, enseignant ou administrateur) :
// c'est le bas de la pile depuis la connexion, donc tout est supprimé et le bouton
// "retour" ne ramène plus sur un écran réservé à un compte connecté.
private fun NavController.logoutToHome(racine: String) {
    navigate(Routes.HOME) {
        popUpTo(racine) { inclusive = true }
        launchSingleTop = true
    }
}

// Changer d'onglet dans la barre du bas
private fun NavController.goTab(route: String, start: String) {
    navigate(route) {
        popUpTo(start) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    // Mode API : charge les événements validés au démarrage
    LaunchedEffect(Unit) { AppState.refreshPublic() }

    NavHost(navController = nav, startDestination = Routes.SPLASH) {

        // ---------- Espace public ----------
        composable(Routes.SPLASH) {
            SplashScreen(onStart = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
            })
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                // Le rôle est deviné d'après l'email (plus tard : fourni par le backend)
                onLogin = {
                    val target = when (AppState.role) {
                        Role.TEACHER -> Routes.TEACHER_HOME
                        Role.ADMIN -> Routes.ADMIN_HOME
                        else -> Routes.STUDENT_HOME
                    }
                    nav.navigate(target) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onRegister = { nav.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBack = { nav.popBackStack() },
                onRegister = { nav.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogin = { nav.navigate(Routes.LOGIN) },
                onSeeAll = { nav.navigate(Routes.EVENTS) },
                onEventClick = { id -> nav.navigate(Routes.eventDetail(id)) }
            )
        }

        composable(Routes.EVENTS) {
            EventListScreen(
                onBack = { nav.popBackStack() },
                onEventClick = { id -> nav.navigate(Routes.eventDetail(id)) }
            )
        }

        composable(
            route = Routes.EVENT_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            EventDetailScreen(
                eventId = entry.arguments?.getInt("id") ?: 0,
                onBack = { nav.popBackStack() },
                actionText = "Connectez-vous pour vous inscrire",
                onAction = { nav.navigate(Routes.LOGIN) }
            )
        }

        // ---------- Espace étudiant ----------
        composable(Routes.STUDENT_HOME) {
            StudentScaffold(Routes.STUDENT_HOME, { nav.goTab(it, Routes.STUDENT_HOME) }) {
                StudentHomeScreen(
                    onEventClick = { id -> nav.navigate(Routes.studentEvent(id)) },
                    onSearch = { nav.goTab(Routes.STUDENT_SEARCH, Routes.STUDENT_HOME) },
                    onSeeRegs = { nav.goTab(Routes.STUDENT_REGS, Routes.STUDENT_HOME) }
                )
            }
        }

        composable(Routes.STUDENT_SEARCH) {
            StudentScaffold(Routes.STUDENT_SEARCH, { nav.goTab(it, Routes.STUDENT_HOME) }) {
                StudentSearchScreen(onEventClick = { id -> nav.navigate(Routes.studentEvent(id)) })
            }
        }

        composable(Routes.STUDENT_REGS) {
            StudentScaffold(Routes.STUDENT_REGS, { nav.goTab(it, Routes.STUDENT_HOME) }) {
                StudentRegistrationsScreen(onEventClick = { id -> nav.navigate(Routes.studentEvent(id)) })
            }
        }

        composable(Routes.STUDENT_PROFILE) {
            StudentScaffold(Routes.STUDENT_PROFILE, { nav.goTab(it, Routes.STUDENT_HOME) }) {
                StudentProfileScreen(onLogout = {
                    AppState.logout()
                        nav.logoutToHome(Routes.STUDENT_HOME)
                })
            }
        }

        // Détails d'un événement avec le bouton S'inscrire
        composable(
            route = Routes.STUDENT_EVENT,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            val event = AppState.byId(id)
            var message by remember { mutableStateOf<String?>(null) }

            val registered = AppState.isRegistered(id)
            val full = event != null && AppState.isFull(event) && !registered

            EventDetailScreen(
                eventId = id,
                onBack = { nav.popBackStack() },
                actionText = when {
                    registered -> "Se désinscrire"
                    full -> "Événement complet"
                    else -> "S'inscrire"
                },
                actionEnabled = !full,
                message = message,
                onAction = {
                    if (event != null) {
                        if (registered) {
                            AppState.unregister(id)
                            message = null
                        } else if (AppState.register(event)) {
                            // RG9
                            message = "Inscription confirmée"
                        }
                    }
                }
            )
        }

        // ---------- Espace enseignant ----------
        composable(Routes.TEACHER_HOME) {
            TeacherScaffold(Routes.TEACHER_HOME, { nav.goTab(it, Routes.TEACHER_HOME) }) {
                TeacherDashboardScreen(
                    onAdd = { nav.goTab(Routes.TEACHER_ADD, Routes.TEACHER_HOME) },
                    onSeeEvents = { nav.goTab(Routes.TEACHER_EVENTS, Routes.TEACHER_HOME) }
                )
            }
        }

        composable(Routes.TEACHER_EVENTS) {
            TeacherScaffold(Routes.TEACHER_EVENTS, { nav.goTab(it, Routes.TEACHER_HOME) }) {
                TeacherEventsScreen(
                    onEdit = { id -> nav.navigate(Routes.teacherEdit(id)) },
                    onParticipants = { id -> nav.navigate(Routes.teacherParticipants(id)) }
                )
            }
        }

        composable(Routes.TEACHER_ADD) {
            TeacherScaffold(Routes.TEACHER_ADD, { nav.goTab(it, Routes.TEACHER_HOME) }) {
                EventFormScreen(
                    eventId = null,
                    onBack = null,
                    onDone = { nav.goTab(Routes.TEACHER_EVENTS, Routes.TEACHER_HOME) }
                )
            }
        }

        composable(Routes.TEACHER_PROFILE) {
            TeacherScaffold(Routes.TEACHER_PROFILE, { nav.goTab(it, Routes.TEACHER_HOME) }) {
                StudentProfileScreen(
                    role = "TEACHER",
                    onLogout = {
                        AppState.logout()
                        nav.logoutToHome(Routes.TEACHER_HOME)
                    }
                )
            }
        }

        composable(
            route = Routes.TEACHER_EDIT,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            EventFormScreen(
                eventId = entry.arguments?.getInt("id") ?: 0,
                onBack = { nav.popBackStack() },
                onDone = { nav.popBackStack() }
            )
        }

        composable(
            route = Routes.TEACHER_PARTICIPANTS,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            ParticipantsScreen(
                eventId = entry.arguments?.getInt("id") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }

        // ---------- Espace admin ----------
        composable(Routes.ADMIN_HOME) {
            AdminScaffold(Routes.ADMIN_HOME, { nav.goTab(it, Routes.ADMIN_HOME) }) {
                AdminDashboardScreen(
                    onSeeEvents = { nav.goTab(Routes.ADMIN_EVENTS, Routes.ADMIN_HOME) },
                    onSeeUsers = { nav.goTab(Routes.ADMIN_USERS, Routes.ADMIN_HOME) }
                )
            }
        }

        composable(Routes.ADMIN_USERS) {
            AdminScaffold(Routes.ADMIN_USERS, { nav.goTab(it, Routes.ADMIN_HOME) }) {
                AdminUsersScreen()
            }
        }

        composable(Routes.ADMIN_EVENTS) {
            AdminScaffold(Routes.ADMIN_EVENTS, { nav.goTab(it, Routes.ADMIN_HOME) }) {
                AdminEventsScreen(
                    onAdd = { nav.navigate(Routes.ADMIN_ADD) },
                    onParticipants = { id -> nav.navigate(Routes.adminParticipants(id)) }
                )
            }
        }

        composable(
            route = Routes.ADMIN_PARTICIPANTS,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            ParticipantsScreen(
                eventId = entry.arguments?.getInt("id") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.ADMIN_ADD) {
            EventFormScreen(
                eventId = null,
                onBack = { nav.popBackStack() },
                onDone = { nav.popBackStack() }
            )
        }

        composable(Routes.ADMIN_PROFILE) {
            AdminScaffold(Routes.ADMIN_PROFILE, { nav.goTab(it, Routes.ADMIN_HOME) }) {
                StudentProfileScreen(
                    role = "ADMIN",
                    onLogout = {
                        AppState.logout()
                        nav.logoutToHome(Routes.ADMIN_HOME)
                    }
                )
            }
        }
    }
}

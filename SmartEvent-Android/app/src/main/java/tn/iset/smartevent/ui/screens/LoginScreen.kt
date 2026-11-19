package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import tn.iset.smartevent.data.api.ApiConfig
import tn.iset.smartevent.ui.theme.StatusRed
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.AppTextField
import tn.iset.smartevent.ui.components.PrimaryButton
import tn.iset.smartevent.ui.components.ServerDialog
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate300
import tn.iset.smartevent.ui.theme.Slate500
import tn.iset.smartevent.ui.theme.White

// E2 / T1 / A1 : écran de connexion.
// Mode API : vérification de l'email et du mot de passe par le serveur (JWT).
// Mode fictif : le rôle est deviné d'après l'email.
@Composable
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var serveurOuvert by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (serveurOuvert) ServerDialog(onClose = { serveurOuvert = false })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
    ) {
        // En-tête bleu nuit
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Navy900)
                .statusBarsPadding()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SMART", fontSize = 14.sp, color = Slate300)
            Text("EVENT", fontSize = 22.sp, color = White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("Connexion", fontSize = 24.sp, color = White, fontWeight = FontWeight.Bold)
            Text("Accédez à vos événements universitaires", fontSize = 13.sp, color = Cloud)
        }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(email, { email = it }, "Email", "prenom.nom@iset.tn", Icons.Default.Email)
            AppTextField(
                password, { password = it }, "Mot de passe", "••••••••",
                Icons.Default.Lock, isPassword = true
            )
            Spacer(Modifier.height(4.dp))
            if (error != null) {
                Text(error!!, color = StatusRed, fontSize = 13.sp)
            }
            PrimaryButton(if (loading) "Connexion..." else "Se connecter", {
                if (email.isNotBlank() && !loading) {
                    if (ApiConfig.USE_API) {
                        loading = true
                        scope.launch {
                            val err = AppState.loginApi(email, password)
                            loading = false
                            if (err == null) onLogin() else error = err
                        }
                    } else {
                        AppState.login(email)
                        onLogin()
                    }
                }
            })
            TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Pas encore de compte ? ", color = Slate500, fontSize = 13.sp)
                Text("Créer un compte", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Permet de changer l'adresse du PC qui héberge le backend sans refaire l'APK
            if (ApiConfig.USE_API) {
                TextButton(
                    onClick = { serveurOuvert = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Adresse du serveur", color = Slate500, fontSize = 12.sp)
                }
            }
        }
    }
}

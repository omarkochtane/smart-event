package tn.iset.smartevent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.iset.smartevent.data.AppState
import tn.iset.smartevent.ui.components.AppTextField
import tn.iset.smartevent.ui.components.BackButton
import tn.iset.smartevent.ui.components.PrimaryButton
import tn.iset.smartevent.ui.theme.Cloud
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.Slate500

// E1 : création d'un compte étudiant (RG7 : seuls les étudiants s'inscrivent eux-mêmes).
// Champs obligatoires : nom, email, mot de passe (4 caractères minimum)
// et numéro de téléphone (8 chiffres minimum).
@Composable
fun RegisterScreen(onBack: () -> Unit, onRegister: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
            BackButton(onBack)
            Spacer(Modifier.width(12.dp))
            Text("Créer un compte", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy900)
        }
        Text(
            "Votre compte est créé en tant qu'étudiant. L'administrateur peut ensuite vous passer enseignant.",
            fontSize = 12.sp,
            color = Slate500
        )
        AppTextField(name, { name = it }, "Nom complet", "Prénom Nom", Icons.Default.Person)
        AppTextField(email, { email = it }, "Email", "prenom.nom@iset.tn", Icons.Default.Email)
        AppTextField(
            phone, { phone = it }, "Téléphone", "Ex : 22 123 456",
            Icons.Default.Phone, keyboardType = KeyboardType.Phone
        )
        AppTextField(
            password, { password = it }, "Mot de passe", "Minimum 4 caractères",
            Icons.Default.Lock, isPassword = true
        )
        AppTextField(
            confirm, { confirm = it }, "Confirmer le mot de passe", "Retapez le mot de passe",
            Icons.Default.Lock, isPassword = true
        )
        Spacer(Modifier.height(4.dp))
        if (error != null) {
            Text(error!!, color = StatusRed, fontSize = 13.sp)
        }
        PrimaryButton("S'inscrire", {
            val digits = phone.filter { it.isDigit() }
            // Validation des champs avant l'envoi au serveur
            if (name.isBlank() || email.isBlank()) {
                error = "Nom et email obligatoires"
            } else if (digits.length < 8) {
                error = "Numéro de téléphone obligatoire (8 chiffres minimum)"
            } else {
                error = null
                // Mode API : le serveur crée le compte et renvoie un jeton JWT
                if (ApiConfig.USE_API) {
                    when {
                        password.length < 4 -> error = "Mot de passe trop court"
                        password != confirm -> error = "Les mots de passe ne correspondent pas"
                        else -> scope.launch {
                            val err = AppState.registerApi(name, email, password, phone)
                            if (err == null) onRegister() else error = err
                        }
                    }
                } else {
                    AppState.signUp(name, email)
                    onRegister()
                }
            }
        })
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Déjà un compte ? ", color = Slate500, fontSize = 13.sp)
            Text("Se connecter", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

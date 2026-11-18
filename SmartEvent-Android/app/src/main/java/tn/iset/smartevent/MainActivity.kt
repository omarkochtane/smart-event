package tn.iset.smartevent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tn.iset.smartevent.navigation.AppNavigation
import tn.iset.smartevent.ui.theme.SmartEventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartEventTheme {
                AppNavigation()
            }
        }
    }
}

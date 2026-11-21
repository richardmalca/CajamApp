package pe.cajamapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import pe.cajamapp.ui.screens.MainScreen
import pe.cajamapp.ui.theme.CajamAppTheme
import pe.cajamapp.ui.viewmodel.DestinationsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DestinationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            CajamAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

package ai.botstacks.sample

import ai.botstacks.sdk.BotStacksChatController
import ai.botstacks.sdk.ui.BotStacksChatContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BotStacksChatContext(useDarkTheme = true) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    val openBotstacks = { navController.navigate("botstacks") }
                    val openLogin = { navController.navigate("login") }

                    composable("splash") {
                        Splash(openChat = openBotstacks, openLogin = openLogin)
                    }
                    composable("login") {
                        Login(openBotstacks) {

                        }
                    }
                    composable("botstacks") {
                        BotStacksChatController { navController.navigate("login") }
                    }
                }
            }
        }
    }
}
package hu.ait.tovisitmapapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.tovisitmapapp.ui.screen.MapScreen
import hu.ait.tovisitmapapp.ui.screen.OpeningScreen
import hu.ait.tovisitmapapp.ui.screen.ToVisitListScreen
import hu.ait.tovisitmapapp.ui.theme.ToVisitMapAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToVisitMapAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ToVisitMapAppNavHost()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ToVisitMapAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "openingscreen"
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {
        composable("openingscreen"){ OpeningScreen(
            onNavigateToMap = {
                navController.navigate("map")
            }

        )}
        composable("tovisitlist") {
            ToVisitListScreen(
                onNavigateToMap = {
                    navController.navigate("map")
                }
            )
        }

        composable("tovisitlist/{name}",
            arguments = listOf(
                navArgument("name"){type = NavType.StringType}
            )) {
            var name = it.arguments?.getString("name")
            if (name == null) {
                name = ""
            }
            ToVisitListScreen(
                name = name,
                onNavigateToMap = {
                    navController.navigate("map")
                }
            )
        }
        composable("tovisitlist") { ToVisitListScreen(
            onNavigateToMap = {
                navController.navigate("map")
            }
        )}

        composable("map") {
            MapScreen(
                onNavigateToToVisitList = {searchName ->
                    navController.navigate("tovisitlist$searchName")
                }
            )
        }
    }
}
package hu.ait.tovisitmapapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.tovisitmapapp.ui.screen.MapScreen
import hu.ait.tovisitmapapp.ui.screen.ToVisitListScreen
import hu.ait.tovisitmapapp.ui.theme.ToVisitMapAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToVisitMapAppTheme {
                // A surface container using the 'background' color from the theme
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

@Composable
fun ToVisitMapAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "map"
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {
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

        composable("map") {
            MapScreen(
                onNavigateToToVisitList = {searchName ->
                    navController.navigate("tovisitlist$searchName")
                }
            )
        }
    }

//        composable("waitingscreen") { SplashScreen(
//            onNavigateToList = {navController.navigate("shoppinglist")}
//        )}
//
//        composable("summary/{food}/{electronics}/{book}",
//            arguments = listOf(
//                navArgument("food"){type = NavType.IntType},
//                navArgument("electronics"){type = NavType.IntType},
//                navArgument("book"){type = NavType.IntType}
//            )
//        ) {
//            val food = it.arguments?.getInt("food")
//            val electronics = it.arguments?.getInt("electronics")
//            val book = it.arguments?.getInt("book")
//            if (food != null && electronics != null && book != null) {
//                SummaryScreen(
//                    food = food,
//                    electronics = electronics,
//                    book = book
//                )
//            }
//        }
    }



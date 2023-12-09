package hu.ait.tovisitmapapp.ui.screen

import android.Manifest
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import hu.ait.tovisitmapapp.R
import kotlinx.coroutines.delay


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OpeningScreen(mapViewModel: MyMapViewModel = hiltViewModel(),
                  onNavigateToMap: () -> Unit) = Box(
    Modifier
        .fillMaxSize()
) {
    val scale = remember {
        Animatable(0.0f)
    }

    val fineLocationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(key1 = Unit) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(800, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
        // 3 second delay then navigate to main screen
//        delay(3000)

        if (fineLocationPermissionState.status.isGranted) {
            onNavigateToMap()
        }
        else(
            delay(10000000)
        )



    }

    Text(
        text = "a new way to keep track of where you want to go!",
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 60.dp)
    )


    Image(
        painter = painterResource(id = R.drawable.travellist),
        contentDescription = "Opening Icon",
        alignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize().padding(40.dp)
            .scale(scale.value)
    )

    Button(onClick = {
        mapViewModel.startLocationMonitoring()

    }) {
        Text(text = "Give permission to start location monitoring?")
    }

}


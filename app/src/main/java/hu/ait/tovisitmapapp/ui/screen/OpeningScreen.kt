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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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


@Composable
fun OpeningScreen(
    onNavigateToMap: () -> Unit
) = Box(Modifier.fillMaxSize()) {
    val scale = remember {
        Animatable(0.0f)
    }

    val robotoFont = FontFamily(
        Font(R.font.roboto, FontWeight.Light)
    )

    Text(
        text = stringResource(R.string.travelling),
        textAlign = TextAlign.Center,
        fontFamily = robotoFont,
        fontSize = 30.sp,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 60.dp)
    )

    LaunchedEffect(key1 = Unit) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(800, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
        delay(1000)
        onNavigateToMap()
    }

    Text(
        text = stringResource(R.string.a_new_way_to_keep_track_of_where_you_want_to_go),
        textAlign = TextAlign.Center,
        fontFamily = robotoFont,
        fontSize = 26.sp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 60.dp)
    )

    Image(
        painter = painterResource(id = R.drawable.travellist),
        contentDescription = stringResource(R.string.opening_icon),
        alignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .scale(scale.value)
    )
}


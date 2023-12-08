package hu.ait.tovisitmapapp.ui.screen

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Random

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // API key:
    // AIzaSyDyN5nBZKlSi-56GMEE2Wqy5fMlhMBhYMw

    // local state with initial coords
    var cameraState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(
            LatLng(47.0, 19.0), 10f // zoom between 1 and 25
        )
    }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true
            )
        )
    }

    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = true
            )
        )
    }

    var geocodeText by remember {
        mutableStateOf("")
    }

    var showSearchDialog by remember {
        mutableStateOf(false)
    }

    Column {
        TopAppBar(
            title = {
                Text("To Visit List")
            },
            actions = {
                IconButton(onClick = {
                    showSearchDialog = true
                }) {
                    Icon(Icons.Filled.Info, null)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )



        val fineLocationPermissionState = rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (fineLocationPermissionState.status.isGranted) {
            Column {

                Button(onClick = {
                    mapViewModel.startLocationMonitoring()
                }) {
                    Text(text = "Start location monitoring")
                }
                Text(
                    text = "Location: ${getLocationText(mapViewModel.locationState.value)}"
                )
            }

        } else {
            Column() {
                val permissionText = if (fineLocationPermissionState.status.shouldShowRationale) {
                    "Please consider giving permission"
                } else {
                    "Give permission for location"
                }
                Text(text = permissionText)
                Button(onClick = {
                    fineLocationPermissionState.launchPermissionRequest()
                }) {
                    Text(text = "Request permission")
                }
            }
        }

        var isSatellite by remember {
            mutableStateOf(false)
        }
        Switch(checked = isSatellite, onCheckedChange = {
            isSatellite = it
            mapProperties = mapProperties.copy(
                mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL
            )
        })

        Text(text = geocodeText)


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapClick = {
                mapViewModel.addMarkerPosition(it)

//                val cameraPosition = CameraPosition.Builder()
//                    .target(it)
//                    .build()

                val random = Random(System.currentTimeMillis())
                val cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .zoom(1f + random.nextInt(5))
                    .tilt(30f + random.nextInt(15))
                    .bearing(-45f + random.nextInt(90))
                    .build()

                coroutineScope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        1000)
                }
            }
        ) {


            Marker(
                state = MarkerState(LatLng(47.0, 19.0)),
                title = "Marker demo",
                snippet = "Hungary, population 9.7M",
                draggable = true
//                icon =
            )

            for (position in mapViewModel.getMarkersList()) {
                Marker(
                    state = MarkerState(position = position),
                    title = "Title",
                    onClick = {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(
                                it.position.latitude,
                                it.position.longitude,
                                3,
                                object : Geocoder.GeocodeListener {
                                    override fun onGeocode(addrs: MutableList<Address>) {
                                        val addr =
                                            "${addrs[0].getAddressLine(0)}; ${
                                                addrs[0].getAddressLine(
                                                    1
                                                )
                                            }; ${addrs[0].getAddressLine(2)}"

                                        geocodeText = addr
                                    }

                                    override fun onError(errorMessage: String?) {
                                        geocodeText = errorMessage!!
                                        super.onError(errorMessage)

                                    }
                                })
                        }
                        true
                    }
                )
            }

            Polyline( // also Polygon - connects last and first points!
                points = listOf(
                    LatLng(47.0, 19.0),
                    LatLng(45.0, 18.0),
                    LatLng(49.0, 23.0),
                ),
                color = androidx.compose.ui.graphics.Color.Red,
                visible = true,
                width = 10f
            )

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchToVisitListDialog(
    onDialogDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDialogDismiss
    ) {
        var searchName by rememberSaveable {
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .padding(10.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchName,
                onValueChange = {
                    searchName = it
                },
                label = { Text(text = "Enter search parameter - leave empty if none.") }
            )
        }

        Button(onClick = {

        }) {
            Text(text = "Search")
        }
    }
}

fun getLocationText(location: Location?): String {
    return """
       Lat: ${location?.latitude}
       Lng: ${location?.longitude}
       Alt: ${location?.altitude}
       Speed: ${location?.speed}
       Accuracy: ${location?.accuracy}
    """.trimIndent()
}
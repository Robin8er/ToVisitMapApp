package hu.ait.tovisitmapapp.ui.screen

import android.Manifest

import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.ait.tovisitmapapp.R
import hu.ait.tovisitmapapp.data.ToVisitCategory
import hu.ait.tovisitmapapp.data.ToVisitItem
import kotlinx.coroutines.launch
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel(),
    toVisitListViewModel: ToVisitListViewModel = hiltViewModel(),
    onNavigateToToVisitList: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val robotoFont = FontFamily(
        Font(R.font.roboto, FontWeight.Light)
    )

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

    var showSearchDialog by remember {
        mutableStateOf(false)
    }

    var showAddLocationDialog by remember {
        mutableStateOf(false)
    }

    var currentPosition by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    var currentLocation by remember {
        mutableStateOf(if (mapViewModel.locationState.value != null)
            LatLng(mapViewModel.locationState.value!!.latitude,
                mapViewModel.locationState.value!!.longitude)
        else LatLng(47.56251658, 19.05498918))
    }

    var firstClick by remember {
        mutableStateOf(true)
    }

    val locationsList by toVisitListViewModel.getAllToVisitList().collectAsState(emptyList())

    Column {


        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.the_map), fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold)
            },

            actions = {

                var isSatellite by remember {
                    mutableStateOf(false)
                }
                Switch(checked = isSatellite, onCheckedChange = {
                    isSatellite = it
                    mapProperties = mapProperties.copy(
                        mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL
                    )
                })

                val fineLocationPermissionState = rememberPermissionState(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                IconButton(onClick = {
                    if (fineLocationPermissionState.status.isGranted) {
                        mapViewModel.startLocationMonitoring()
                        if (!firstClick) {
                            currentLocation = if (mapViewModel.locationState.value != null)
                                LatLng(mapViewModel.locationState.value!!.latitude,
                                    mapViewModel.locationState.value!!.longitude)
                            else LatLng(47.56251658, 19.05498918)
                            val currentCameraLocation = CameraPosition.Builder()
                                .target(currentLocation)
                                .zoom(15f)
                                .build()
                            coroutineScope.launch {
                                cameraState.animate(
                                    CameraUpdateFactory.newCameraPosition(currentCameraLocation),
                                    1000)
                            }
                        } else {
                            firstClick = false
                        }
                    } else {
                        fineLocationPermissionState.launchPermissionRequest()
                    }
                }) {
                    Icon(Icons.Filled.LocationOn, null)
                }

                IconButton(onClick = {
                    onNavigateToToVisitList("")
                }) {
                    Icon(Icons.Filled.Menu, null)
                }
                IconButton(onClick = {
                    showSearchDialog = true
                }) {
                    Icon(Icons.Filled.Search, null)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )

        if (showSearchDialog) {
            SearchToVisitListDialog({ showSearchDialog = false }, onNavigateToToVisitList)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapClick = {
                currentPosition = it
                showAddLocationDialog = true
                val cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .zoom(17.5f)
                    .build()

                coroutineScope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        1000)
                }
            }
        ) {


            for (location in locationsList) {
                val posLatLng = LatLng(location.latitude, location.longitude)
                val priorityEmoji = if (location.priority < 0.25f) "🧍"
                    else if (location.priority > 0.75f) "🏃"
                        else "🚶"
                Marker(
                    state = MarkerState(position = posLatLng),
                    title = "${location.name} $priorityEmoji",
                    snippet = location.description,
                    icon = if (location.haveVisited) BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }
        }

        if (showAddLocationDialog) {
            AddLocationForm(toVisitListViewModel,
                {
                    showAddLocationDialog = false
                },
                currentPosition)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddLocationForm(
    toVisitListViewModel: ToVisitListViewModel,
    onDialogDismiss: () -> Unit = {},
    position: LatLng,
    toVisitItemToEdit: ToVisitItem? = null
) {
    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var toVisitItemName by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.name ?: "")
        }

        var toVisitItemDescription by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.description ?: "")
        }

        var toVisitItemCategory by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.category ?: ToVisitCategory.DINING)
        }

        var toVisitItemVisited by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.haveVisited ?: false
            )
        }

        var toVisitItemAddress by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.address ?: "")
        }

        var toVisitItemLatitude by rememberSaveable {
            mutableDoubleStateOf(position.latitude)
        }

        var toVisitItemLongitude by rememberSaveable {
            mutableDoubleStateOf(position.longitude)
        }

        var toVisitItemPriority by remember {
            mutableFloatStateOf(toVisitItemToEdit?.priority ?: 0.5f)
        }

        var nameError by rememberSaveable {mutableStateOf(false)}


        var geocodeText = stringResource(R.string.declaring)
        val context = LocalContext.current
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocodeText = geocoder.getFromLocation(
                    position.latitude,
                    position.longitude,
                    3)?.get(0)?.getAddressLine(0) ?: ""
            }
        } catch (e: Exception) {
            geocodeText = stringResource(R.string.n_a)
        }

        toVisitItemAddress = geocodeText

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
                value = toVisitItemName,
                onValueChange = {
                    toVisitItemName = it
                    nameError = toVisitItemName == ""
                },
                label = { Text(text = stringResource(R.string.name_of_this_location)) },
                trailingIcon = {
                    if (nameError) {
                        Icon(
                            Icons.Filled.Warning, stringResource(R.string.error),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            if (nameError) {
                Text(
                    text = stringResource(R.string.name_cannot_be_empty),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = toVisitItemDescription,
                onValueChange = {
                    toVisitItemDescription = it
                },
                label = { Text(text = stringResource(R.string.description_of_location)) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(R.string.priority))
            Slider(
                value = toVisitItemPriority,
                onValueChange = { toVisitItemPriority = it }
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(text = "🧍",
                    fontSize = 30.sp)
                Text(text = "🚶",
                    fontSize = 30.sp)
                Text(text = "🏃",
                    fontSize = 30.sp)
            }


            SpinnerSample(
                listOf(stringResource(R.string.dining),
                    stringResource(R.string.study),
                    stringResource(R.string.entertainment),
                    stringResource(R.string.other)
                ),
                preselected =
                when (toVisitItemCategory)
                {
                    ToVisitCategory.DINING -> stringResource(R.string.dining)
                    ToVisitCategory.STUDY -> stringResource(R.string.study)
                    ToVisitCategory.ENTERTAINMENT -> stringResource(R.string.entertainment)
                    else -> stringResource(R.string.other)
                },
                onSelectionChanged = {
                    toVisitItemCategory =
                        when (it) {
                            "Dining" -> ToVisitCategory.DINING
                            "Study" -> ToVisitCategory.STUDY
                            "Entertainment" -> ToVisitCategory.ENTERTAINMENT
                            else -> ToVisitCategory.OTHER
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = toVisitItemVisited, onCheckedChange = { toVisitItemVisited = it })
                Text(text = stringResource(R.string.visited_map))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (toVisitItemName == "") {
                        nameError = true
                    }
                    else if (toVisitItemToEdit == null) {
                        toVisitListViewModel.addToVisitItem(
                            ToVisitItem(
                                0,
                                toVisitItemName,
                                toVisitItemDescription,
                                toVisitItemPriority,
                                toVisitItemCategory,
                                toVisitItemVisited,
                                toVisitItemAddress,
                                toVisitItemLatitude,
                                toVisitItemLongitude
                            )
                        )
                        onDialogDismiss()
                    } else {
                        var toVisitItemEdited = toVisitItemToEdit.copy(
                            name = toVisitItemName,
                            description = toVisitItemDescription,
                            priority = toVisitItemPriority,
                            category = toVisitItemCategory,
                            haveVisited = toVisitItemVisited,
                            address = toVisitItemAddress,
                            latitude = toVisitItemLatitude,
                            longitude =toVisitItemLongitude
                        )
                        toVisitListViewModel.editToVisitItem(toVisitItemEdited)
                        onDialogDismiss()
                    }
                }) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}




@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchToVisitListDialog(
    onDialogDismiss: () -> Unit = {},
    onNavigateToToVisitList: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDialogDismiss
    ) {
        var searchName by rememberSaveable {
            mutableStateOf("")
        }

        var nameError by rememberSaveable {
            mutableStateOf(false)
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
                    nameError = searchName == ""
                },
                label = { Text(text = stringResource(R.string.search_for_locations_with)) },
                trailingIcon = {
                    if (nameError) {
                    Icon(
                        Icons.Filled.Warning, stringResource(R.string.error),
                        tint = MaterialTheme.colorScheme.error)
                }}
            )

            if (nameError) {
                Text(
                    text = stringResource(R.string.search_field_cannot_be_empty),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Button(onClick = {
                if (searchName != "") {
                    searchName = "/$searchName"
                    onNavigateToToVisitList(searchName)
                } else {
                    nameError = true
                }
            }) {
                Text(text = stringResource(R.string.search))
            }
        }
    }
}
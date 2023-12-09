package hu.ait.tovisitmapapp.ui.screen

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import hu.ait.tovisitmapapp.data.ToVisitCategory
import hu.ait.tovisitmapapp.data.ToVisitItem
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Random
import androidx.compose.runtime.LaunchedEffect as LaunchedEffect1

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel(),
    toVisitListViewModel: ToVisitListViewModel = hiltViewModel(),
    onNavigateToToVisitList: (String) -> Unit
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

    var showSearchDialog by remember {
        mutableStateOf(false)
    }

    var showAddLocationDialog by remember {
        mutableStateOf(false)
    }

    var currentPosition by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    val latList by toVisitListViewModel.getAllLatitudes().collectAsState(emptyList())

    val longList by toVisitListViewModel.getAllLongitudes().collectAsState(emptyList())

    Column {
        TopAppBar(
            title = {
                Text("To Visit List")
            },
            actions = {
                IconButton(onClick = {
                    onNavigateToToVisitList("")
                }) {
                    Icon(Icons.Filled.Info, null)
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
//                Text(
//                    text = "Location: ${getLocationText(mapViewModel.locationState.value)}"
//                )
            }

        } else {
            Column {
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

        //Text(text = geocodeText)

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
                //add a dialogue here!!! and the info added creates a card on the other screen

                val cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .build()

                coroutineScope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        1000)
                }
            }
        ) {


            for (i in (latList.indices)) {
                val curLat = latList[i]
                val curLong = longList[i]
                var posLatLng = LatLng(curLat, curLong)
                Marker(
                    state = MarkerState(position = posLatLng),
                    title = "Name that the user gives location here",
                    snippet = "information that user gives..."
                )
            }
        }

        if (showAddLocationDialog) {
            AddLocationForm(toVisitListViewModel,
                {
                    showAddLocationDialog = false
//                    mapViewModel.addMarkerPosition(currentPosition)
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

        var toVisitItemPriority by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.priority ?: "")
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

        var nameError by rememberSaveable {mutableStateOf(false)}
        var priorityError by rememberSaveable {mutableStateOf(false)}

        var geocodeText = "Error"
        val context = LocalContext.current
        val geocoder = Geocoder(context, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocodeText = geocoder.getFromLocation(
                position.latitude,
                position.longitude,
                3)?.get(0)?.getAddressLine(0) ?: "Error"
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
                label = { Text(text = "Enter name of place to visit") },
                trailingIcon = {
                    if (nameError) {
                        Icon(
                            Icons.Filled.Warning, "Error",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            if (nameError) {
                Text(
                    text = "Name cannot be empty.",
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
                label = { Text(text = "Enter description of place here.") }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = toVisitItemPriority,
                onValueChange = {
                    toVisitItemPriority = it
                },
                label = { Text(text = "Enter priority of place here") },//TODO: maybe turn into a spinner?
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    if (priorityError) {
                        Icon(
                            Icons.Filled.Warning, "Error",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            if (priorityError) {
                Text(
                    text = "Priority cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            SpinnerSample(
                listOf("Dining",
                    "Study", "Entertainment", "Other"
                ),
                preselected =
                when (toVisitItemCategory)
                {
                    ToVisitCategory.DINING -> "Dining"
                    ToVisitCategory.STUDY -> "Study"
                    ToVisitCategory.ENTERTAINMENT -> "Entertainment"
                    else -> "Other"
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
                Text(text = "Visited")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (toVisitItemName == "" || toVisitItemPriority == "" || priorityError) {
                        if (toVisitItemName == "") {
                            nameError = true
                        }
                        if (toVisitItemPriority == "") {
                            priorityError = true
                        }
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
                    Text(text = "Save")
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
                label = { Text(text = "Search for - leave empty") },
                trailingIcon = {
                    if (nameError) {
                    Icon(
                        Icons.Filled.Warning, "Error",
                        tint = MaterialTheme.colorScheme.error)
                }}
            )

            if (nameError) {
                Text(
                    text = "Search field cannot be empty.",
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
                Text(text = "Search")
            }
        }
    }
}
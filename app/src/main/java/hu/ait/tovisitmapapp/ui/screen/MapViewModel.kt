package hu.ait.tovisitmapapp.ui.screen

import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.tovisitmapapp.location.LocationManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMapViewModel @Inject constructor(
    val locationManager: LocationManager
) : ViewModel() {

    // --- Maps related
    private var _markerPositionList =
        mutableStateListOf<LatLng>()

    fun getMarkersList(): List<LatLng> {
        return _markerPositionList
    }

    fun addMarkerPosition(latLng: LatLng) {
        _markerPositionList.add(latLng)

    }

    // --- location related functions!
    var locationState = mutableStateOf<Location?>(null)

    fun startLocationMonitoring() {
        viewModelScope.launch {
            locationManager
                .fetchUpdates()
                .collect {
                    locationState.value = it
                }
        }
    }

}
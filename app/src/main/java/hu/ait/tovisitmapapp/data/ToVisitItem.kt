package hu.ait.tovisitmapapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import hu.ait.tovisitmapapp.R
import java.io.Serializable

@Entity(tableName = "tovisittable")
data class ToVisitItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "priority") var priority: Float,
    @ColumnInfo(name = "category") var category: ToVisitCategory,
    @ColumnInfo(name = "havevisited") var haveVisited: Boolean,
    @ColumnInfo(name = "address") var address: String,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double
) : Serializable

enum class ToVisitCategory {
    DINING, STUDY, ENTERTAINMENT, OTHER;

    fun getIcon(): Int {
        return if (this == DINING) R.drawable.dining
        else if (this == STUDY) R.drawable.study
        else if (this == ENTERTAINMENT) R.drawable.entertainment
        else R.drawable.other
    }
}
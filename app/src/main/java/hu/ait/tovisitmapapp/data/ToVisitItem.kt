package hu.ait.tovisitmapapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.ait.tovisitmapapp.R
import java.io.Serializable

@Entity(tableName = "tovisittable")
data class ToVisitItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "priority") var priority: String, //TODO: maybe make an int / double / float
    @ColumnInfo(name = "category") var category: ToVisitCategory,
    @ColumnInfo(name = "havevisited") var haveVisited: Boolean
) : Serializable

enum class ToVisitCategory {
    DINING, ENTERTAINMENT, MUSEUM, NATURE; //TODO: think of categories

//    fun getIcon(): Int {
//        return if (this == FOOD) R.drawable.food
//        else if (this == ELECTRONICS) R.drawable.electronics
//        else R.drawable.book
//    }
}
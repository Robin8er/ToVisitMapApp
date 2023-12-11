package hu.ait.tovisitmapapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToVisitItem::class], version = 5, exportSchema = false)
abstract class ToVisitListAppDatabase : RoomDatabase() {

    abstract fun toVisitListDao(): ToVisitListDAO

    companion object {
        @Volatile
        private var Instance: ToVisitListAppDatabase? = null

        fun getDatabase(context: Context): ToVisitListAppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ToVisitListAppDatabase::class.java,
                    "to_visit_list_database.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
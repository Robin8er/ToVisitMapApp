package hu.ait.tovisitmapapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToVisitItem::class], version = 3, exportSchema = false)
abstract class ToVisitListAppDatabase : RoomDatabase() {

    abstract fun toVisitListDao(): ToVisitListDAO

    companion object {
        @Volatile
        private var Instance: ToVisitListAppDatabase? = null

        fun getDatabase(context: Context): ToVisitListAppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ToVisitListAppDatabase::class.java,
                    "to_visit_list_database.db")
                    // Setting this option in your app's database builder means that Room
                    // permanently deletes all data from the tables in your database when it
                    // attempts to perform a migration with no defined migration path.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
package hu.ait.tovisitmapapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ToVisitListDAO {
    @Query("SELECT * from tovisittable ORDER BY priority desc, name asc")
    fun getAllToVisitItems(): Flow<List<ToVisitItem>>

    @Query("SELECT * from tovisittable WHERE id = :id")
    fun getToVisitItem(id: Int): Flow<ToVisitItem>

    @Query("SELECT * FROM tovisittable WHERE name LIKE '%' || :name || '%' ORDER BY priority desc, name asc")
    fun getToVisitItemsLike(name: String): Flow<List<ToVisitItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toVisitItem: ToVisitItem)

    @Update
    suspend fun update(toVisitItem: ToVisitItem)

    @Delete
    suspend fun delete(toVisitItem: ToVisitItem)

    @Query("DELETE from tovisittable")
    suspend fun deleteAllToVisitItems()
}
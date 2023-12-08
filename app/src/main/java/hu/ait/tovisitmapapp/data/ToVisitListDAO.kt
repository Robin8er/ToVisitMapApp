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
    @Query("SELECT * from tovisittable")
    fun getAllToVisitItems(): Flow<List<ToVisitItem>>

    @Query("SELECT * from tovisittable WHERE id = :id")
    fun getToVisitItem(id: Int): Flow<ToVisitItem>

    @Query("SELECT * FROM tovisittable WHERE name LIKE name = :name")
    fun getToVisitItemsLike(name: String): Flow<List<ToVisitItem>>

//    @Query("""SELECT COUNT(*) from tovisittable WHERE category="FOOD"""")
//    suspend fun getFoodItemsNum(): Int
//
//    @Query("""SELECT COUNT(*) from tovisittable WHERE category="ELECTRONICS"""")
//    suspend fun getElectronicsItemsNum(): Int
//
//    @Query("""SELECT COUNT(*) from tovisittable WHERE category="BOOK"""")
//    suspend fun getBookItemsNum(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toVisitItem: ToVisitItem)

    @Update
    suspend fun update(toVisitItem: ToVisitItem)

    @Delete
    suspend fun delete(toVisitItem: ToVisitItem)

    @Query("DELETE from tovisittable")
    suspend fun deleteAllToVisitItems()
}
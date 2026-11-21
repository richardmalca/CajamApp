package pe.cajamapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pe.cajamapp.data.model.Destination

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destinations ORDER BY rating DESC")
    fun getAllDestinations(): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE id = :id")
    suspend fun getDestinationById(id: Long): Destination?

    @Query("SELECT * FROM destinations WHERE category = :category")
    fun getDestinationsByCategory(category: String): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE isFavorite = 1")
    fun getFavoriteDestinations(): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR province LIKE '%' || :query || '%'")
    fun searchDestinations(query: String): Flow<List<Destination>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestinations(destinations: List<Destination>)

    @Update
    suspend fun updateDestination(destination: Destination)

    @Query("UPDATE destinations SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT COUNT(*) FROM destinations")
    suspend fun getCount(): Int
}

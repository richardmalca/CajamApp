package pe.cajamapp.data.repository

import kotlinx.coroutines.flow.Flow
import pe.cajamapp.data.local.DestinationDao
import pe.cajamapp.data.model.Destination

class DestinationRepository(private val dao: DestinationDao) {
    val allDestinations: Flow<List<Destination>> = dao.getAllDestinations()
    val favoriteDestinations: Flow<List<Destination>> = dao.getFavoriteDestinations()

    suspend fun getDestinationById(id: Long): Destination? = dao.getDestinationById(id)

    fun getDestinationsByCategory(category: String): Flow<List<Destination>> =
        dao.getDestinationsByCategory(category)

    fun searchDestinations(query: String): Flow<List<Destination>> =
        dao.searchDestinations(query)

    suspend fun toggleFavorite(id: Long) {
        dao.toggleFavorite(id)
    }

    suspend fun ensureDataPopulated(initialList: List<Destination>) {
        if (dao.getCount() == 0) {
            dao.insertDestinations(initialList)
        }
    }
}

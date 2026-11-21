package pe.cajamapp.ui.viewmodel

import android.app.Application
import java.text.Normalizer
import java.util.Locale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.cajamapp.data.local.AppDatabase
import pe.cajamapp.data.local.CajamarcaData
import pe.cajamapp.data.model.Destination
import pe.cajamapp.data.repository.DestinationRepository

data class UserLocation(val latitude: Double, val longitude: Double)

class DestinationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DestinationRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _selectedDestination = MutableStateFlow<Destination?>(null)

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    val categories = listOf("Todos") + CajamarcaData.getInitialDestinations().map { it.category }.distinct()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = DestinationRepository(database.destinationDao())
        viewModelScope.launch {
            repository.ensureDataPopulated(CajamarcaData.getInitialDestinations())
        }
    }

    val selectedDestination: StateFlow<Destination?> = combine(
        _selectedDestination, repository.allDestinations
    ) { selected, all -> selected?.let { item -> all.find { it.id == item.id } ?: item } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val destinations: StateFlow<List<Destination>> = combine(
        repository.allDestinations,
        _searchQuery,
        _selectedCategory
    ) { all, query, category ->
        val term = query.trim().searchKey()
        all.filter { dest ->
            val matchesQuery = term.isEmpty() ||
                dest.name.searchKey().contains(term) ||
                dest.description.searchKey().contains(term) ||
                dest.province.searchKey().contains(term)

            val matchesCategory = (category == "Todos") || dest.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteDestinations: StateFlow<List<Destination>> = repository.favoriteDestinations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun selectDestination(destination: Destination?) {
        _selectedDestination.value = destination
    }

    fun selectDestinationById(id: Long) {
        viewModelScope.launch {
            val dest = repository.getDestinationById(id)
            _selectedDestination.value = dest
        }
    }

    fun toggleFavorite(destination: Destination) {
        viewModelScope.launch {
            repository.toggleFavorite(destination.id)
        }
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        _userLocation.value = UserLocation(latitude, longitude)
    }
}

private val combiningMarks = Regex("\\p{M}+")

private fun String.searchKey(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(combiningMarks, "").lowercase(Locale.ROOT)

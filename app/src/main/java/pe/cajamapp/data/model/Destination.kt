package pe.cajamapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val province: String,
    val description: String,
    val howToGetThere: String,
    val transportType: String,
    val travelTime: String,
    val estimatedFare: String,
    val entryFee: String,
    val openingHours: String,
    val altitude: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false,
    val rating: Double = 4.8,
    val tips: String = ""
)

package by.ssrlab.data.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaceEntity(

    @PrimaryKey
    val pk: Int,
    val keyName: String,
    val logo: String,
    val lat: Double,
    val lon: Double,

    @Embedded
    val image: ImageEntity
)

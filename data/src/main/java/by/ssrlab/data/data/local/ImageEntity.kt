package by.ssrlab.data.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageEntity(

    @PrimaryKey
    val id: Int,
    val address: String
)

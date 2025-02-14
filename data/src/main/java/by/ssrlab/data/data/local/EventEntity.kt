package by.ssrlab.data.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EventEntity(

    @PrimaryKey
    val pk: Int,
    val startDate: String,
    val endDate: String,
    val keyName: String
)

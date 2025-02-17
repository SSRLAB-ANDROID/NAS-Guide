package by.ssrlab.data.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DepartmentFilterEntity(

    @PrimaryKey
    val pk: Int,
    val keyName: String
)
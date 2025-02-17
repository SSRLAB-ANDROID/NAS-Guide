package by.ssrlab.data.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LanguageEntity(

    @PrimaryKey
    val id: Int,
    val languageKey: String,
    val languageName: String
)

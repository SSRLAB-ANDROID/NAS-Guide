package by.ssrlab.ui.states

import by.ssrlab.data.data.settings.remote.PersonLocale

data class PersonsState(
    val playerState: PlayerState? = null,
    val playerStatus: PlayerStatus? = null,

    val personList: List<PersonLocale> = listOf(), //instead of personsData
    val title: String = "",
)

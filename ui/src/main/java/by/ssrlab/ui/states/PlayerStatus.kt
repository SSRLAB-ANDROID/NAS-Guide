package by.ssrlab.ui.states

sealed class PlayerStatus {
    data object Playing : PlayerStatus()
    data object Paused : PlayerStatus()
    data object Seeking : PlayerStatus()
    data object Ended : PlayerStatus()
}
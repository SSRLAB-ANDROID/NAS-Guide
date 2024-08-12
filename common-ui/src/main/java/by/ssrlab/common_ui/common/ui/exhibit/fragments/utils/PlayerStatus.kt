package by.ssrlab.common_ui.common.ui.exhibit.fragments.utils

sealed class PlayerStatus {
    data object Playing : PlayerStatus()
    data object Paused : PlayerStatus()
    data object Seeking : PlayerStatus()
    data object Ended : PlayerStatus()
}
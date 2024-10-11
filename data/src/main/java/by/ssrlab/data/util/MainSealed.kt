package by.ssrlab.data.util

sealed class MainActivityUiState {
    data object EventFragment : MainActivityUiState()
    data object Other : MainActivityUiState()
}

sealed class ToolbarStateByDates {
    data object OnCreate : ToolbarStateByDates()
    data object OnDestroy : ToolbarStateByDates()
}

sealed class ButtonAction {
    data object BackAction : ButtonAction()
    data object SearchAction : ButtonAction()
    data object ChooseDateAction : ButtonAction()
    data object LanguageAction : ButtonAction()
}
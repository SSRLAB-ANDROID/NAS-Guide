package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.ssrlab.domain.repository.ui.UiDataProvider
import by.ssrlab.ui.R


class FMainVM(private val uiDataProvider: UiDataProvider): ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val uiDataProvider: UiDataProvider): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FMainVM(uiDataProvider) as T
        }
    }

    private val _title = MutableLiveData("")
    val title: LiveData<String>
        get() = _title

    fun setTitle(value: String) {
        _title.value = value
    }

    fun getData() = uiDataProvider.getMainFolders(
        R.id.action_mainFragment_to_historyFragment,
        R.id.action_mainFragment_to_orgFragment,
        R.id.action_mainFragment_to_inventionsFragment
    )
}
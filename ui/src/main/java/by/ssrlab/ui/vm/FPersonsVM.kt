package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.settings.remote.PersonLocale
import by.ssrlab.domain.repository.network.PersonsRepository
import by.ssrlab.domain.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FPersonsVM(personsRepository: PersonsRepository) :
    BaseFragmentVM<PersonLocale>(personsRepository) {

    private val _personsData = MutableLiveData<Resource<List<PersonLocale>>>()
    val personsData: LiveData<Resource<List<PersonLocale>>> get() = _personsData

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    fun setTitle(value: String) {
        _title.value = value
    }

    private fun loadData() {
        getResourceData(
            onSuccess = { data ->
                _personsData.value = Resource.Success(data)
            },
            onError = { errorMessage ->
                _personsData.value = Resource.Error(errorMessage)
            },
            onLoading = {
                _personsData.value = Resource.Loading
            }
        )
    }

    init {
        loadData()
    }
}
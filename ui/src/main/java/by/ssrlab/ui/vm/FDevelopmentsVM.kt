package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.settings.remote.DevelopmentLocale
import by.ssrlab.domain.repository.network.DevelopmentsRepository
import by.ssrlab.domain.utils.Resource
import java.util.Locale

class FDevelopmentsVM(developmentsRepository: DevelopmentsRepository) :
    BaseFragmentVM<DevelopmentLocale>(developmentsRepository) {

    private val _inventionsData = MutableLiveData<Resource<List<DevelopmentLocale>>>()
    val inventionsData: LiveData<Resource<List<DevelopmentLocale>>> get() = _inventionsData

    private val _title = MutableLiveData("")
    val title: LiveData<String>
        get() = _title

    fun setTitle(value: String) {
        _title.value = value
    }

    private fun loadData() {
        getResourceData(
            onSuccess = { data ->
                _inventionsData.value = Resource.Success(data)
            },
            onError = { errorMessage ->
                _inventionsData.value = Resource.Error(errorMessage)
            },
            onLoading = {
                _inventionsData.value = Resource.Loading
            }
        )
    }

    init {
        loadData()
    }


    private val _filteredData = MutableLiveData<List<DevelopmentLocale>>()
    val filteredDataList: LiveData<List<DevelopmentLocale>> get() = _filteredData

    private fun setFilteredList(value: List<DevelopmentLocale>) {
        _filteredData.value = value
    }

    fun filterData(query: String) {
        val queryLowered = query.lowercase(Locale.getDefault())
        val resource = _inventionsData.value
        if (resource is Resource.Success) {
            val filteredList = resource.data.filter {
                it.name.contains(queryLowered, ignoreCase = true)
            }
            setFilteredList(filteredList)
        }
    }
}
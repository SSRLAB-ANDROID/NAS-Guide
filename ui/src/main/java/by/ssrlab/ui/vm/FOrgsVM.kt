package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.common.DescriptionData
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.domain.repository.network.OrgsRepository
import by.ssrlab.domain.utils.Resource
import java.util.Locale

class FOrgsVM(orgsRepository: OrgsRepository): BaseFragmentVM<OrganizationLocale>(orgsRepository) {

    private val _title = MutableLiveData("")
    val title: LiveData<String>
        get() = _title

    fun setTitle(value: String) {
        _title.value = value
    }


    private val _orgsData = MutableLiveData<Resource<List<OrganizationLocale>>>()
    val orgsData: LiveData<Resource<List<OrganizationLocale>>> get() = _orgsData

    fun getDescriptionArray(): ArrayList<DescriptionData> {
        val array = arrayListOf<DescriptionData>()

        val resource = _orgsData.value
        if (resource is Resource.Success) {
            for (place in resource.data) {
                array.add(place.description)
            }
        }

        return array
    }

    private fun loadData() {
        getResourceData(
            onSuccess = { data ->
                _orgsData.value = Resource.Success(data)
            },
            onError = { errorMessage ->
                _orgsData.value = Resource.Error(errorMessage)
            },
            onLoading = {
                _orgsData.value = Resource.Loading
            }
        )
    }

    init {
        loadData()
    }


    private val _filteredData = MutableLiveData<List<OrganizationLocale>>()
    val filteredDataList: LiveData<List<OrganizationLocale>> get() = _filteredData

    private fun setFilteredList(value: List<OrganizationLocale>) {
        _filteredData.value = value
    }

    fun filterData(query: String) {
        val queryLowered = query.lowercase(Locale.getDefault())
        val resource = _orgsData.value
        if (resource is Resource.Success) {
            val filteredList = resource.data.filter {
                it.name.contains(queryLowered, ignoreCase = true)
            }
            setFilteredList(filteredList)
        }
    }
}
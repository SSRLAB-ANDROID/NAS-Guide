package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.common.DescriptionData
import by.ssrlab.data.data.settings.remote.PlaceLocale
import by.ssrlab.domain.repository.network.PlacesRepository
import by.ssrlab.domain.utils.Resource
import java.util.Locale


class FPlacesVM(placesRepository: PlacesRepository): BaseFragmentVM<PlaceLocale>(placesRepository) {

    private val _placesData = MutableLiveData<Resource<List<PlaceLocale>>>()
    val placesData: LiveData<Resource<List<PlaceLocale>>> get() = _placesData

    private val _title = MutableLiveData("")
    val title: LiveData<String>
        get() = _title

    private val _isLoaded = MutableLiveData(false)
    val isLoaded: LiveData<Boolean> = _isLoaded

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setLoaded(value: Boolean) {
        _isLoaded.value = value
    }

    fun getDescriptionArray(): ArrayList<DescriptionData> {
        val array = arrayListOf<DescriptionData>()

        val resource = _placesData.value
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
                _placesData.value = Resource.Success(data)
            },
            onError = { errorMessage ->
                _placesData.value = Resource.Error(errorMessage)
            },
            onLoading = {
                _placesData.value = Resource.Loading
            }
        )
    }

    init {
        loadData()
    }


    private val _filteredData = MutableLiveData<List<PlaceLocale>>()
    val filteredDataList: LiveData<List<PlaceLocale>> get() = _filteredData

    private fun setFilteredList(value: List<PlaceLocale>) {
        _filteredData.value = value
    }

    fun filterData(query: String) {
        val queryLowered = query.lowercase(Locale.getDefault())
        val resource = _placesData.value
        if (resource is Resource.Success) {
            val filteredList = resource.data.filter {
                it.name.contains(queryLowered, ignoreCase = true)
            }
            setFilteredList(filteredList)
        }
    }
}
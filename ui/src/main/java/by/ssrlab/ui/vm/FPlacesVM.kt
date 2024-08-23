package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.common.DescriptionData
import by.ssrlab.data.data.settings.remote.PlaceLocale
import by.ssrlab.domain.repository.network.PlacesRepository
import by.ssrlab.domain.utils.Resource

class FPlacesVM(placesRepository: PlacesRepository): BaseFragmentVM<PlaceLocale>(placesRepository) {

    private val _placesData = MutableLiveData<Resource<List<PlaceLocale>>>()
    val placesData: LiveData<Resource<List<PlaceLocale>>> get() = _placesData

    private val _title = MutableLiveData("")
    val title: LiveData<String>
        get() = _title

    fun setTitle(value: String) {
        _title.value = value
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
}
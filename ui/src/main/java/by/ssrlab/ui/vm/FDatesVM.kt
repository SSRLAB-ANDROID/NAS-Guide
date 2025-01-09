package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.settings.remote.EventLocale
import by.ssrlab.domain.repository.network.EventsRepository
import by.ssrlab.domain.utils.Resource


class FDatesVM(eventsRepository: EventsRepository) : BaseFragmentVM<EventLocale>(eventsRepository) {

    private val _datesData = MutableLiveData<Resource<List<EventLocale>>>()
    val datesData: LiveData<Resource<List<EventLocale>>> get() = _datesData

    val datesObservableBoolean = MutableLiveData(false)

    fun updateEvents(events: List<EventLocale>) {
        _datesData.value = Resource.Success(events.toList())
    }

    init {
        loadData()
    }


    private fun loadData() {
        getResourceData(
            onSuccess = { events ->
                _datesData.value = Resource.Success(events)
                datesObservableBoolean.value = true
            },
            onError = { errorMessage ->
                _datesData.value = Resource.Error(errorMessage)
            },
            onLoading = {
                _datesData.value = Resource.Loading
            }
        )
    }
}
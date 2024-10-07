package by.ssrlab.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.settings.remote.EventLocale
import by.ssrlab.domain.repository.network.EventsRepository

class FDatesVM(eventsRepository: EventsRepository) : BaseFragmentVM<EventLocale>(eventsRepository) {

    private val _datesData = MutableLiveData<List<EventLocale>>(listOf())
    val datesData: LiveData<List<EventLocale>> get() = _datesData

    val datesObservableBoolean = MutableLiveData(false)

    fun updateEvents(events: List<EventLocale>) {
        _datesData.value = events
    }

    init {
        getData {
            getData {
                _datesData.value = it
                datesObservableBoolean.value = true
            }
        }
    }
}
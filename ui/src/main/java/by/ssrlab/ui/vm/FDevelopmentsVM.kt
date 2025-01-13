package by.ssrlab.ui.vm

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.ssrlab.common_ui.common.ui.base.vm.BaseFragmentVM
import by.ssrlab.data.data.remote.DepartmentFilter
import by.ssrlab.data.data.settings.remote.DevelopmentLocale
import by.ssrlab.domain.repository.network.DevelopmentsRepository
import by.ssrlab.domain.utils.Resource
import by.ssrlab.domain.utils.transformLanguageToInt
import kotlinx.coroutines.launch
import java.util.Locale

class FDevelopmentsVM(developmentsRepository: DevelopmentsRepository) :
    BaseFragmentVM<DevelopmentLocale>(developmentsRepository) {

    private val _inventionsData = MutableLiveData<Resource<List<DevelopmentLocale>>>()
    val inventionsData: LiveData<Resource<List<DevelopmentLocale>>> get() = _inventionsData

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

    //Language change
    private var currentLanguage: String? = "en"

    fun observeLanguageChanges() {
        viewModelScope.launch {
            val newLanguage = getSelectedLanguage()
            if (currentLanguage != newLanguage) {
                currentLanguage = newLanguage
                loadData()
            }
        }
    }

    init {
        loadData()
    }


    //Search
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


    //Filter
    private val _isFiltering = MutableLiveData(false)
    val isFiltering: LiveData<Boolean> = _isFiltering

    private val _availableFilters = MutableLiveData<Map<DepartmentFilter, Int>>(emptyMap())
    val availableFilters: LiveData<Map<DepartmentFilter, Int>> = _availableFilters

    private val _filterList = MutableLiveData<List<DepartmentFilter>>(emptyList())
    val filterList: LiveData<List<DepartmentFilter>> = _filterList

    private val _selectedFilters = MutableLiveData<Set<DepartmentFilter>>(emptySet())
    val selectedFilters: LiveData<Set<DepartmentFilter>> = _selectedFilters

    fun setFiltering(value: Boolean) {
        _isFiltering.value = value
    }

    fun setAvailableFilters() {
        val currentLanguageKey = getSelectedLanguage()
        val uniqueDepartmentFilters: Set<DepartmentFilter> =
            if (_inventionsData.value is Resource.Success) {
                (_inventionsData.value as Resource.Success<List<DevelopmentLocale>>).data
                    .map { invention ->
                        val localizedFilterName = invention.description.translations
                            .find { it.language.languageKey == currentLanguageKey }
                            ?.name ?: invention.description.departmentFilter.keyName

                        invention.description.departmentFilter.copy(
                            keyName = localizedFilterName
                        )
                    }
                    .toSet()
            } else {
                emptySet()
            }

        _filterList.value = uniqueDepartmentFilters.toList()

        val departmentFilterCounts: Map<DepartmentFilter, Int> =
            uniqueDepartmentFilters.associateWith { filter ->
                _inventionsData.value?.let {
                    if (it is Resource.Success) {
                        it.data.count { invention -> invention.description.translations[currentLanguageKey.transformLanguageToInt()-1].name == filter.keyName }
                    } else {
                        0
                    }
                } ?: 0
            }

        _availableFilters.value = departmentFilterCounts
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun onFilterSelected(filter: DepartmentFilter, isSelected: Boolean) {
        val currentFilters = _selectedFilters.value?.toMutableSet()

        if (isSelected) {
            currentFilters?.add(filter)
        } else {
            currentFilters?.remove(filter)
        }
        currentFilters?.let {
            _selectedFilters.value = currentFilters
        }
    }

    fun applyFilters() {
        val currentLanguageKey = getSelectedLanguage()

        val filterDataByChoices =
            if (_inventionsData.value is Resource.Success) {
                val currentSelectedFilters = _selectedFilters.value
                (_inventionsData.value as Resource.Success<List<DevelopmentLocale>>).data.filter { element ->
                    val localizedFilterName = element.description.translations
                        .find { it.language.languageKey == currentLanguageKey }
                        ?.name

                    currentSelectedFilters?.any { selectedFilter ->
                        selectedFilter.keyName == localizedFilterName
                    } ?: false
                }
            } else {
                emptyList()
            }

        setFilteredList(filterDataByChoices)
    }

    fun resetFilters() {
        _selectedFilters.value = emptySet()
    }
}
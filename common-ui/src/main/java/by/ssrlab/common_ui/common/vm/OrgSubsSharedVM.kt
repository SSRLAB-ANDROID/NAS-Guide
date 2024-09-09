package by.ssrlab.common_ui.common.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.ssrlab.data.data.settings.remote.OrganizationLocale

class OrgSubsSharedVM : ViewModel() {

    private val _orgList = MutableLiveData<List<OrganizationLocale>>(listOf())
    val orgList: LiveData<List<OrganizationLocale>> get() = _orgList

    fun setOrgList(orgList: List<OrganizationLocale>) {
        _orgList.value = orgList
    }

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    fun setTitle(title: String) {
        _title.value = title
    }
}
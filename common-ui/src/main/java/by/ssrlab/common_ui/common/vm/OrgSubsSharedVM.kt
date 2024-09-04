package by.ssrlab.common_ui.common.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.ssrlab.data.data.settings.remote.OrganizationLocale

class OrgSubsSharedVM : ViewModel() {

    private val _orgList = MutableLiveData<List<OrganizationLocale>>()
    val orgList: LiveData<List<OrganizationLocale>> get() = _orgList

    fun setOrgList(orgList: List<OrganizationLocale>) {
        _orgList.value = orgList
    }
}
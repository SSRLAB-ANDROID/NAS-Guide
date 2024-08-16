package by.ssrlab.common_ui.common.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.ssrlab.data.data.common.RepositoryData

class AExhibitVM: ViewModel() {

    private val _header = MutableLiveData("")
    val header: LiveData<String>
        get() = _header

    fun setHeader(res: String?) {
        if (res != null) {
            _header.value = res
        } else _header.value = ""
    }

    private val _repositoryData = MutableLiveData<RepositoryData?>(null)
    val repositoryData: LiveData<RepositoryData?> get() = _repositoryData

    fun setData(value: RepositoryData?) {
        _repositoryData.value = value as RepositoryData
    }
}
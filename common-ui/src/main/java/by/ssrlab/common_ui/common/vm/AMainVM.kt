package by.ssrlab.common_ui.common.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.ssrlab.common_ui.R
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import java.util.Calendar

class AMainVM: ViewModel() {

    private val _headerImg = MutableLiveData(0)
    val headerImg: LiveData<Int>
        get() = _headerImg

    fun setHeaderImg(resource: Int) {
        _headerImg.value = resource
    }

    //Control buttons
    private val _isBackVisible = MutableLiveData(false)
    private val _isLangVisible = MutableLiveData(false)
    private val _isSearchVisible = MutableLiveData(false)
    private val _isDatesVisible = MutableLiveData(false)

    val isBackVisible: LiveData<Boolean> get() = _isBackVisible
    val isLangVisible: LiveData<Boolean> get() = _isLangVisible
    val isSearchVisible: LiveData<Boolean> get() = _isSearchVisible
    val isDatesVisible: LiveData<Boolean> get() = _isDatesVisible

    fun setupButtons(toolbarControlObject: ToolbarControlObject) {
        _isBackVisible.value = toolbarControlObject.isBack
        _isLangVisible.value = toolbarControlObject.isLang
        _isSearchVisible.value = toolbarControlObject.isSearch
        _isDatesVisible.value = toolbarControlObject.isDates
    }

    private val _backAction = MutableLiveData<() -> Unit>(::emptyAction)
    private val _searchAction = MutableLiveData<() -> Unit>(::emptyAction)
    private val _chooseDateAction = MutableLiveData<() -> Unit>(::emptyAction)
    private val _languageAction = MutableLiveData<() -> Unit>(::emptyAction)

    val backAction: LiveData<() -> Unit> get() = _backAction
    val searchAction: LiveData<() -> Unit> get() = _searchAction
    val chooseDateAction: LiveData<() -> Unit> get() = _chooseDateAction
    val languageAction: LiveData<() -> Unit> get() = _languageAction

    fun setButtonAction(buttonAction: ButtonAction, action: () -> Unit) {
        when (buttonAction) {
            ButtonAction.BackAction -> _backAction.value = action
            ButtonAction.ChooseDateAction -> _chooseDateAction.value = action
            ButtonAction.SearchAction -> _searchAction.value = action
            ButtonAction.LanguageAction -> _languageAction.value = action
        }
    }

    private fun emptyAction() {}

    //Date section
    private val _currentDate = MutableLiveData("")
    val currentDate: LiveData<String> get() = _currentDate

    private val _currentDateNumeric = MutableLiveData("")
    val currentDateNumeric: LiveData<String> get() = _currentDateNumeric

    private val _dateSubtitle = MutableLiveData("")
    val dateSubtitle: LiveData<String> get() = _dateSubtitle

    fun refreshDate(context: Context) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)

        _dateSubtitle.value = context.resources.getString(R.string.today)
        _currentDate.value = "$day ${formatMonth(month, context)}"
        updateDateNumeric(day, month)
    }

    fun onDateChanged(day: Int, month: Int, context: Context) {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.DAY_OF_MONTH) == day && calendar.get(Calendar.MONTH) == month)
            _dateSubtitle.value = context.resources.getString(R.string.today)
        else _dateSubtitle.value = context.resources.getString(R.string.date)

        _currentDate.value = "$day ${formatMonth(month, context)}"
        updateDateNumeric(day, month)
    }

    private fun updateDateNumeric(day: Int, month: Int) {
        var monthStr = (month + 1).toString()
        var dayStr = day.toString()
        if (monthStr.length == 1) monthStr = "0$monthStr"
        if (dayStr.length == 1) dayStr = "0$dayStr"
        _currentDateNumeric.value = "$monthStr-$dayStr"
    }

    private fun formatMonth(month: Int, context: Context): String {
        return when (month) {
            0 -> context.resources.getString(R.string.january)
            1 -> context.resources.getString(R.string.february)
            2 -> context.resources.getString(R.string.march)
            3 -> context.resources.getString(R.string.april)
            4 -> context.resources.getString(R.string.may)
            5 -> context.resources.getString(R.string.june)
            6 -> context.resources.getString(R.string.july)
            7 -> context.resources.getString(R.string.august)
            8 -> context.resources.getString(R.string.september)
            9 -> context.resources.getString(R.string.october)
            10 -> context.resources.getString(R.string.november)
            11 -> context.resources.getString(R.string.december)
            else -> context.resources.getString(R.string.january)
        }
    }
}
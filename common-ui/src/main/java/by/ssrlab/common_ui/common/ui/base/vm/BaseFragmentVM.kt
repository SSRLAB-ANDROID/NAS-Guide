package by.ssrlab.common_ui.common.ui.base.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.domain.models.SharedPreferencesUtil
import by.ssrlab.domain.repository.network.base.BaseRepository
import by.ssrlab.domain.utils.transformLanguageToInt
import by.ssrlab.domain.utils.unwrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseFragmentVM<T : RepositoryData>(private val baseRepository: BaseRepository<T>) :
    ViewModel(), KoinComponent {

    private val networkScope = CoroutineScope(Dispatchers.IO + Job())
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())
    private val sharedPreferences: SharedPreferencesUtil by inject()

    fun getData(onSuccess: (List<T>) -> Unit) {
        networkScope.launch {
            baseRepository.get(sharedPreferences.getLanguage()!!.transformLanguageToInt()).unwrap()
                ?.enqueue(object : Callback<List<T>> {
                    override fun onResponse(p0: Call<List<T>>, p1: Response<List<T>>) {
                        uiScope.launch { onSuccess(p1.body() ?: listOf()) }
                    }

                    override fun onFailure(p0: Call<List<T>>, p1: Throwable) {
                        Log.e(REQUEST_ERROR_LOG, p1.message.toString())
                    }
                })
        }
    }

    fun getResourceData(
        onSuccess: (List<T>) -> Unit,
        onError: (String) -> Unit,
        onLoading: (() -> Unit)? = null
    ) {
        onLoading?.invoke()
        networkScope.launch {
            baseRepository.get(sharedPreferences.getLanguage()!!.transformLanguageToInt()).unwrap()
                ?.enqueue(object : Callback<List<T>> {
                    override fun onResponse(p0: Call<List<T>>, p1: Response<List<T>>) {
                        if (p1.isSuccessful) {
                            uiScope.launch { onSuccess(p1.body() ?: listOf()) }
                        } else {
                            uiScope.launch { onError("Failed with code: ${p1.code()}") }
                        }
                    }

                    override fun onFailure(p0: Call<List<T>>, p1: Throwable) {
                        Log.e(REQUEST_ERROR_LOG, p1.message.toString())
                        onError(p1.message ?: "An unknown error occurred")
                    }
                })
        }
    }

    companion object {
        private const val REQUEST_ERROR_LOG = "request_error_log"
    }
}
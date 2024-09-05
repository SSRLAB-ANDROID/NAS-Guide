package by.ssrlab.common_ui.common.ui.base

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import by.ssrlab.common_ui.common.ui.map.MapActivity
import by.ssrlab.common_ui.common.util.createDateDialog
import by.ssrlab.common_ui.common.util.createSimpleAlertDialog
import by.ssrlab.data.data.common.DescriptionData
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.data.remote.Development
import by.ssrlab.data.data.remote.Organization
import by.ssrlab.data.data.remote.Person
import by.ssrlab.data.data.remote.Place
import by.ssrlab.domain.models.SharedPreferencesUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

open class BaseActivity: AppCompatActivity(), KoinComponent {

    private fun Context.loadPreferences(): Context {
        val sharedPreferences: SharedPreferencesUtil by inject()

        val locale = Locale(sharedPreferences.getLanguage()!!)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return createConfigurationContext(config)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ContextWrapper(newBase?.loadPreferences()))
    }

    fun createIsntRealizedDialog() {
        createSimpleAlertDialog(
            this@BaseActivity.getString(by.ssrlab.common_ui.R.string.dialog_dont_available),
            this@BaseActivity.getString(by.ssrlab.common_ui.R.string.dialog_isnt_realized),
            this@BaseActivity.getString(by.ssrlab.common_ui.R.string.dialog_ok),
            this@BaseActivity
        )
    }

    fun createDatePickerDialog(onDateChanged: (Int, Int) -> Unit) {
        createDateDialog(this, onDateChanged)
    }

    fun getLocale(): Locale {
        return resources.configuration.locales.get(0)
    }

    fun moveToMapFromExhibit(repositoryData: RepositoryData) {
        if (this !is MapActivity) {
            val list = ArrayList(listOf(repositoryData.description!!))

            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(MAPBOX_VIEW_POINT_LIST, setMapParcelableData(list))
            startActivity(intent)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setMapParcelableData(descriptionData: ArrayList<DescriptionData>): ArrayList<Parcelable> {
        return when (descriptionData[0]) {
            is Development -> descriptionData as ArrayList<Parcelable>
            is Organization -> descriptionData as ArrayList<Parcelable>
            is Person -> descriptionData as ArrayList<Parcelable>
            is Place -> descriptionData as ArrayList<Parcelable>
            else -> descriptionData as ArrayList<Parcelable>
        }
    }

    companion object {
        const val PARCELABLE_DATA = "parcelable_data"
        //        const val MAPBOX_VIEW_POINT = "mapbox_view_point"
        const val MAPBOX_VIEW_POINT_LIST = "mapbox_view_point_list"
        const val MAPBOX_LOCATION_RECHECK_TIME = 2000L
    }
}

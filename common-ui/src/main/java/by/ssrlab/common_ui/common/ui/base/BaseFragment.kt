package by.ssrlab.common_ui.common.ui.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import by.ssrlab.common_ui.common.util.createLanguageDialog
import by.ssrlab.common_ui.common.vm.AMainVM
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.domain.models.SharedPreferencesUtil
import by.ssrlab.domain.models.ToolbarControlObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

abstract class BaseFragment : Fragment() {

    abstract val fragmentViewModel: ViewModel
    abstract val toolbarControlObject: ToolbarControlObject

    val activityVM: AMainVM by activityViewModel()
    private val sharedPreferences: SharedPreferencesUtil by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as AppCompatActivity).onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }

        initActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityVM.setupButtons(toolbarControlObject)
        return initBinding(container)
    }

    abstract fun initBinding(container: ViewGroup?): View
    open fun onBackPressed() {}
    open fun initActivity() {}
    open fun navigateNext(address: Int) {}
    open fun navigateNext(repositoryData: RepositoryData) {}
    open fun initAdapter() {}
    open fun observeOnDataChanged() {}
    open fun filterData(query: String) {}
    open fun hideSearchBar(){}

    fun initLanguageDialog() {
        createLanguageDialog(requireActivity(), sharedPreferences) {
            requireActivity().recreate()
        }
    }

    fun loadImage(imageView: ImageView, imageId: Int) {
        val resources = imageView.context.resources
        val bitmap = BitmapFactory.decodeResource(resources, imageId)
        val roundedDrawable = createRoundedDrawable(resources, bitmap)
        imageView.setImageDrawable(roundedDrawable)
    }

    private fun createRoundedDrawable(
        resources: android.content.res.Resources,
        bitmap: Bitmap
    ): RoundedBitmapDrawable {
        val roundedDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedDrawable.cornerRadius = 20f
        return roundedDrawable
    }
}
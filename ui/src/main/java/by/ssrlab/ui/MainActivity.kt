package by.ssrlab.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.common_ui.common.ui.exhibit.ExhibitActivity
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.ActivityMainMarginParams
import by.ssrlab.common_ui.common.ui.map.MapActivity
import by.ssrlab.common_ui.common.vm.AMainVM
import by.ssrlab.data.data.common.DescriptionData
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.data.remote.Development
import by.ssrlab.data.data.remote.Organization
import by.ssrlab.data.data.remote.Person
import by.ssrlab.data.data.remote.Place
import by.ssrlab.data.data.settings.remote.DevelopmentLocale
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.data.data.settings.remote.PersonLocale
import by.ssrlab.data.data.settings.remote.PlaceLocale
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.data.util.MainActivityUiState
import by.ssrlab.data.util.ToolbarStateByDates
import by.ssrlab.ui.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val activityViewModel: AMainVM by viewModel()

    private lateinit var container: ConstraintLayout
    private lateinit var transition: AutoTransition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.lifecycleOwner = this@MainActivity
        binding.viewModel = activityViewModel

        activityViewModel.apply {
            setButtonAction(ButtonAction.BackAction, ::createIsntRealizedDialog)
            setButtonAction(ButtonAction.SearchAction, ::createIsntRealizedDialog)
            setButtonAction(ButtonAction.LanguageAction, ::createIsntRealizedDialog)
            setButtonAction(ButtonAction.ChooseDateAction) {
                createDatePickerDialog { day, month ->
                    onDateChanged(day, month, this@MainActivity)
                }
            }

            refreshDate(this@MainActivity)
        }

        observeLayoutChange()
        observeHeader()
        observeButtonsState()
    }

    private fun observeLayoutChange() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val statusBarHeight = getPadding(ActivityMainMarginParams.STATUS_HEIGHT)
                val navBarHeight = getPadding(ActivityMainMarginParams.NAVIGATION_HEIGHT)

                val toolbarParams = binding.toolbar.layoutParams as ConstraintLayout.LayoutParams
                toolbarParams.topMargin = statusBarHeight
                binding.toolbar.layoutParams = toolbarParams

                val bottomBlurParams = binding.activityBottomBlur.layoutParams
                bottomBlurParams.height = navBarHeight
                binding.activityBottomBlur.layoutParams = bottomBlurParams
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun observeHeader() {
        activityViewModel.headerImg.observe(this) {
            val currentDrawable = binding.activityHeader.drawable
            val newDrawable = resources.getDrawable(it, null)

            if (currentDrawable != null) {
                val transitionDrawable = TransitionDrawable(arrayOf(currentDrawable, newDrawable))
                binding.activityHeader.setImageDrawable(transitionDrawable)
                transitionDrawable.startTransition(100)
            } else {
                binding.activityHeader.setImageDrawable(newDrawable)
            }
        }
    }

    private fun observeButtonsState() {
        container = binding.toolbar
        transition = AutoTransition().apply {
            duration = 75
            interpolator = AccelerateDecelerateInterpolator()
        }

        activityViewModel.apply {
            isBackVisible.observe(this@MainActivity) { animateButton(binding.toolbarBack, it) }
            isLangVisible.observe(this@MainActivity) { animateButton(binding.toolbarLang, it) }
            isSearchVisible.observe(this@MainActivity) { animateButton(binding.toolbarSearch, it) }
            isDatesVisible.observe(this@MainActivity) { animateButton(binding.toolbarDate, it) }
        }
    }

    @Suppress("DiscouragedApi")
    private fun getPadding(identifier: ActivityMainMarginParams): Int {
        var result = 0
        val resourceId = resources.getIdentifier(identifier.param, "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }

        return result
    }

    private fun animateButton(button: View, visible: Boolean) {
        TransitionManager.beginDelayedTransition(container, transition)

        if (visible) button.visibility = View.VISIBLE
        else button.visibility = View.GONE
    }

    fun moveToExhibit(repositoryData: RepositoryData) {
        val intent = Intent(this, ExhibitActivity::class.java)
        intent.putExtra(PARCELABLE_DATA, setParcelableData(repositoryData))
        startActivity(intent)
    }

    fun moveToMap(descriptionData: ArrayList<DescriptionData>) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra(MAPBOX_VIEW_POINT_LIST, setMapParcelableData(descriptionData))
        startActivity(intent)
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

    private fun setParcelableData(repositoryData: RepositoryData): Parcelable {
        return when (repositoryData) {
            is DevelopmentLocale -> repositoryData
            is OrganizationLocale -> repositoryData
            is PersonLocale -> repositoryData
            is PlaceLocale -> repositoryData
            else -> repositoryData as Parcelable
        }
    }

    fun changeLayoutState(mainActivityUiState: MainActivityUiState) {
        val constraintLayout = binding.mainActivity
        val constraintSet = ConstraintSet().apply { clone(constraintLayout) }

        when (mainActivityUiState) {
            MainActivityUiState.DateFragment -> {
                constraintSet.connect(
                    binding.activityMainFragmentHost.id,
                    ConstraintSet.TOP,
                    binding.activityHeader.id,
                    ConstraintSet.BOTTOM
                )
                constraintSet.applyTo(constraintLayout)
                binding.datesTitle.visibility = View.VISIBLE
            }

            MainActivityUiState.Other -> {
                constraintSet.connect(
                    binding.activityMainFragmentHost.id,
                    ConstraintSet.TOP,
                    binding.toolbar.id,
                    ConstraintSet.BOTTOM
                )
                constraintSet.applyTo(constraintLayout)
                binding.datesTitle.visibility = View.GONE
            }
        }
    }

    fun setupToolbarByDates(toolbarStateByDates: ToolbarStateByDates) {
        when (toolbarStateByDates) {
            ToolbarStateByDates.OnCreate -> {
                binding.datesNowHolder.visibility = View.VISIBLE
            }

            ToolbarStateByDates.OnDestroy -> {
                binding.datesNowHolder.visibility = View.GONE
            }
        }
    }
}
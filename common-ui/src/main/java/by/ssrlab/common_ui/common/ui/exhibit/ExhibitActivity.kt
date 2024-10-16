package by.ssrlab.common_ui.common.ui.exhibit

import android.content.IntentFilter
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import by.ssrlab.common_ui.R
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.ActivityMainMarginParams
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.player.AudioManager
import by.ssrlab.common_ui.common.vm.AExhibitVM
import by.ssrlab.common_ui.databinding.ActivityExhibitBinding
import by.ssrlab.data.util.ExhibitObject
import coil.load
import coil.transform.RoundedCornersTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExhibitActivity : BaseActivity() {

    private lateinit var binding: ActivityExhibitBinding
    private val activityViewModel: AExhibitVM by viewModel()
    private lateinit var audioManager: AudioManager

    @Suppress("DEPRECATION")
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
        audioManager = AudioManager(activityViewModel)

        binding = ActivityExhibitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityViewModel.apply {
            setData(intent.getParcelableExtra(PARCELABLE_DATA))
            if (repositoryData.value != null) observeHeader()
        }

        setUpButtons()
        setUpVolumeStateListener()
        observeLayoutChange()
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(
            audioManager.volumeChangeReceiver,
            IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        )
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(audioManager.volumeChangeReceiver)
    }

    private fun getPlaceId(exhibitObject: ExhibitObject): String {
        return when (exhibitObject) {
            ExhibitObject.Development -> TODO()
            ExhibitObject.Organization -> TODO()
            ExhibitObject.Person -> TODO()
            ExhibitObject.Place -> TODO()
        }
    }

    private fun setUpButtons() {
        setUpVolumeButton()
        setBackAction()
    }

    private fun setUpVolumeButton() {
        if (!activityViewModel.isVolumeOn.value!!) binding.toolbarVolume.setImageResource(R.drawable.toolbar_exhibit_ic_volume_off)
        else binding.toolbarVolume.setImageResource(R.drawable.toolbar_exhibit_ic_volume)

        activityViewModel.isVolumeOn.observe(this) {
            if (!activityViewModel.isVolumeOn.value!!) binding.toolbarVolume.setImageResource(
                R.drawable.toolbar_exhibit_ic_volume_off
            )
            else binding.toolbarVolume.setImageResource(R.drawable.toolbar_exhibit_ic_volume)
        }

        binding.toolbarVolume.setOnClickListener {
            if (activityViewModel.isVolumeOn.value!!) {
                audioManager.controlVolume(0, this)
                Toast.makeText(this, getString(R.string.volume_off), Toast.LENGTH_SHORT).show()
            } else audioManager.controlVolume(7, this)
        }
    }

    private fun setUpVolumeStateListener() {
        audioManager.setUpVolumeStateListener(activityViewModel, this)
    }

    private fun setBackAction() {
        binding.toolbarBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeHeader() {
        activityViewModel.header.observe(this@ExhibitActivity) {
            binding.activityHeader.load(it) {
                transformations(RoundedCornersTransformation(bottomRight = 60f))
            }
        }
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

    @Suppress("DiscouragedApi")
    private fun getPadding(identifier: ActivityMainMarginParams): Int {
        var result = 0
        val resourceId = resources.getIdentifier(identifier.param, "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }

        return result
    }
}
package by.ssrlab.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityOptionsCompat
import by.ssrlab.common_ui.common.ui.MainActivity
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.ui.databinding.ActivityStartBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StartActivity : BaseActivity() {

    private lateinit var binding: ActivityStartBinding
    private var isAnimationStarted = false
    private val animScope = CoroutineScope(Dispatchers.Main + Job())

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

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        loadAnimations()
    }

    private fun loadAnimations() {
        animScope.launch {
            if (!isAnimationStarted) {
                isAnimationStarted = true

                binding.apply {
                    startAnimation(logoPng, AnimationUtils.loadAnimation(this@StartActivity, R.anim.logo_png))
                    delay(500)
                    startAnimation(logoTitle, AnimationUtils.loadAnimation(this@StartActivity, R.anim.logo_title))
                }

                delay(1000)
                moveNext()
            }
        }
    }

    private fun startAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
        view.visibility = View.VISIBLE
    }

    private fun moveNext() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            by.ssrlab.common_ui.R.anim.nav_en,
            by.ssrlab.common_ui.R.anim.nav_ex
        )
        startActivity(intent, options.toBundle())
    }
}
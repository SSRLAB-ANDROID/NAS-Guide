package by.ssrlab.common_ui.common.ui.exhibit.fragments.utils

import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.common_ui.common.ui.exhibit.ExhibitActivity
import by.ssrlab.common_ui.databinding.FragmentExhibitBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object MediaPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private val seekBarFuns = SeekBarFunctions()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    fun initializeMediaPlayerWithString(
        activity: BaseActivity,
        binding: FragmentExhibitBinding,
        url: String,
        onSuccess: () -> Unit
    ) {
        mediaPlayer = MediaPlayer()

        try {
            setDataSource(activity, url.toUri())
            mediaPlayer?.setOnPreparedListener {
                binding.apply {
                    exhibitEndTime.text = seekBarFuns.convertToTimerMode(mediaPlayer!!.duration)
                    exhibitCurrentTime.text =
                        seekBarFuns.convertToTimerMode(mediaPlayer!!.currentPosition)
                }
                listenProgress(binding)
                onSuccess()
            }
        } catch (e: Exception) {
            activity.runOnUiThread {
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handlePlayerState(
        state: PlayerStatus,
        activity: ExhibitActivity,
        binding: FragmentExhibitBinding
    ) {
        when (state) {
            is PlayerStatus.Playing -> {
                try {
                    mediaPlayer?.start()
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            is PlayerStatus.Paused -> {
                mediaPlayer?.pause()
                scope.launch {
                    activity.runOnUiThread {
                        binding.apply {
                            if (mediaPlayer?.isPlaying == true) {
                                exhibitCurrentTime.text =
                                    seekBarFuns.convertToTimerMode(mediaPlayer!!.currentPosition)
                                exhibitProgress.progress = mediaPlayer!!.currentPosition
                            }
                        }
                    }
                }
            }

            is PlayerStatus.Seeking -> {
            }

            is PlayerStatus.Ended -> {
                mediaPlayer?.seekTo(0)
                scope.launch {
                    activity.runOnUiThread {
                        binding.apply {
                            exhibitEndTime.text = "0:00"
                            exhibitCurrentTime.text =
                                seekBarFuns.convertToTimerMode(mediaPlayer!!.currentPosition)
                        }
                    }
                }
            }
        }
    }

    private fun mediaPlayerIsNull(
        audio: String,
        binding: FragmentExhibitBinding,
        exhibitActivity: ExhibitActivity
    ) {
        val fragmentSettingsManager = FragmentSettingsManager(
            binding = binding,
            exhibitActivity = exhibitActivity
        )
        fragmentSettingsManager.initMediaPlayerWithString(audio)
    }

//    fun isPlaying(
//        audio: String,
//        binding: FragmentExhibitBinding,
//        exhibitActivity: ExhibitActivity
//    ): Boolean {
//
//        Log.d("isPlaying", "isPlaying")
//
//        if (mediaPlayer == null) Log.d("isPlaying", "mediaPlayerIsNull") //mediaPlayerIsNull(audio, binding, exhibitActivity)
//
//        Log.d("isPlaying", "isPlaying")
//        return mediaPlayer?.isPlaying ?: false
//    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun playAudio(
        activity: ExhibitActivity,
        binding: FragmentExhibitBinding,
        playerStatus: PlayerStatus
    ) {
        scope.launch {
            when (playerStatus) {
                PlayerStatus.Paused -> {
                    mediaPlayer?.pause()
                }

                PlayerStatus.Playing -> {
                    try {
                        mediaPlayer?.start()
                        handlePlayerState(PlayerStatus.Playing, activity, binding)
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    Toast.makeText(activity, "Not implemented yet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
            it.stop()
            it.reset()
        }
    }

    private fun setDataSource(
        activity: BaseActivity, uri: Uri
    ) {
        try {
            mediaPlayer?.setDataSource(activity, uri)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun listenProgress(binding: FragmentExhibitBinding) {
        binding.exhibitProgress.setOnSeekBarChangeListener(
            seekBarFuns.createSeekBarProgressListener { progress ->
                mediaPlayer?.seekTo(progress)
                scope.launch {
                    binding.exhibitCurrentTime.text =
                        seekBarFuns.convertToTimerMode(mediaPlayer!!.currentPosition)
                }
            }
        )
    }
}
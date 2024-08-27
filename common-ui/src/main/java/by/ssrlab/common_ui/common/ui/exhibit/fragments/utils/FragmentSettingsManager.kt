package by.ssrlab.common_ui.common.ui.exhibit.fragments.utils

import android.view.View
import by.ssrlab.common_ui.R
import by.ssrlab.common_ui.common.ui.exhibit.ExhibitActivity
import by.ssrlab.common_ui.databinding.FragmentExhibitBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentSettingsManager(
    private val binding: FragmentExhibitBinding,
    private val exhibitActivity: ExhibitActivity
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    fun initMediaPlayerWithString(url: String) {
        MediaPlayer.initializeMediaPlayerWithString(exhibitActivity, binding, url) {
            binding.apply {
                scope.launch {
                    exhibitPlayerProgress.visibility = View.INVISIBLE
                    exhibitProgress.visibility = View.INVISIBLE
                    exhibitCurrentTime.visibility = View.VISIBLE
                    exhibitEndTime.visibility = View.VISIBLE
                    exhibitPlay.visibility = View.VISIBLE
                    exhibitPlayRipple.setOnClickListener {
                        if (MediaPlayer.isPlaying()) {
                            exhibitPlayRipple.setImageResource(R.drawable.ic_play)
                            MediaPlayer.playAudio(
                                playerStatus = PlayerStatus.Paused,
                                activity = exhibitActivity,
                                binding = binding
                            )
                        } else {
                            exhibitPlayRipple.setImageResource(R.drawable.ic_pause)
                            MediaPlayer.playAudio(
                                playerStatus = PlayerStatus.Playing,
                                activity = exhibitActivity,
                                binding = binding
                            )
                        }
                    }
                    exhibitPrevious.visibility = View.VISIBLE
//                    exhibitPreviousRipple.setOnClickListener {
//                        MediaPlayer.playAudio(
//                            playerStatus = PlayerStatus.Paused,
//                            activity = exhibitActivity,
//                            binding = binding
//                        )
//                    }
                    exhibitNext.visibility = View.VISIBLE
//                    exhibitNextRipple.setOnClickListener {
//                        MediaPlayer.playAudio(
//                            playerStatus = PlayerStatus.Paused,
//                            activity = exhibitActivity,
//                            binding = binding
//                        )
//                    }
                }
            }
        }
    }

    fun makeProgressVisible() {
        scope.launch() {
            binding.apply {
                exhibitPlayerProgress.visibility = View.VISIBLE
                exhibitProgress.visibility = View.VISIBLE
            }
        }
    }

    fun makeProgressInvisible() {
        scope.launch() {
            binding.apply {
                exhibitPlayerProgress.visibility = View.INVISIBLE
                exhibitProgress.visibility = View.INVISIBLE
                exhibitPlayRipple.setImageResource(R.drawable.ic_play)
            }
        }
    }

    fun destroyPlayer(){
        MediaPlayer.destroyPlayer()
    }
}
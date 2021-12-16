package com.example.android_course.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Adapter
import android.widget.ImageButton
import androidx.core.view.marginStart
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.databinding.FragmentOnboardingBinding
import com.example.android_course.util.onboardingTextAdapterDelegate
import com.example.android_course.ui.base.BaseFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlin.properties.Delegates
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.android_course.ui.signin.SignInViewModel
import timber.log.Timber


class OnBoardingFragment : BaseFragment(R.layout.fragment_onboarding) {
    private val viewBinding by viewBinding(FragmentOnboardingBinding::bind)
    private val viewModel: OnBoardingViewModel by viewModels()

    private var player: ExoPlayer? = null
    private var page = 0
    private var numPages  = 0
    private val handler: Handler by lazy { Handler() }
    private val viewPagerSwitchRoutine by lazy {
        getVIewPagerSwitchRoutine()
    }
    private val delay: Long = 4000




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.soundButton.setBackgroundResource(R.drawable.ic_volume_up_white_24dp)
        player = SimpleExoPlayer.Builder(requireContext()).build().apply {
            addMediaItem(MediaItem.fromUri("asset:///videoplayback.mp4"))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
        viewBinding.playerView.player = player


        viewBinding.viewPager.setTextPages()
        viewBinding.viewPager.attachDots(viewBinding.onboardingTextTabLayout)
        viewBinding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    handler.removeCallbacks(viewPagerSwitchRoutine)
                    page = position
                    handler.postDelayed(viewPagerSwitchRoutine, delay)
                }
            }
        )
        //viewBinding.viewPager.clipToPadding = false;
        //viewBinding.viewPager.setPadding(8, 0, 0, 8)



        viewBinding.signUpButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_onBoardingFragment_to_signUpFragment)
        }
        viewBinding.signInButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_onBoardingFragment_to_signInFragment)
        }


        if(!viewModel.soundEnabled)
            soundOff(viewBinding.soundButton, player)

        viewBinding.soundButton.setOnClickListener {
            if (viewModel.soundEnabled) {
                soundOff(viewBinding.soundButton, player)
            } else {
                soundOn(viewBinding.soundButton, player)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.position?.let {
            player?.seekTo(it)
        }
        player?.play()
        handler.postDelayed(viewPagerSwitchRoutine, delay)

    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        viewModel.position = player?.currentPosition
        handler.removeCallbacks(viewPagerSwitchRoutine)

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        handler.removeCallbacks(viewPagerSwitchRoutine)
    }

    private fun soundOff(soundButton: ImageButton, player: ExoPlayer?) {
        viewModel.currentVolume = player?.volume
        player?.volume = 0f
        viewModel.soundEnabled = false
        soundButton.setBackgroundResource(R.drawable.ic_volume_off_white_24dp)
    }


    private fun soundOn(soundButton: ImageButton, player: ExoPlayer?) {
        player?.volume = viewModel.currentVolume!!
        viewModel.soundEnabled = true
        soundButton.setBackgroundResource(R.drawable.ic_volume_up_white_24dp)
    }


    private fun ViewPager2.setTextPages() {
        adapter =
            ListDelegationAdapter(onboardingTextAdapterDelegate()).apply {
                items =
                    listOf(
                        getString(R.string.onboarding_view_pager_text_1),
                        getString(R.string.onboarding_view_pager_text_2),
                        getString(R.string.onboarding_view_pager_text_3)
                    )
                numPages = items.size
            }
    }

    private fun ViewPager2.attachDots(tabLayout: TabLayout) {
        TabLayoutMediator(tabLayout, this) { _, _ -> }.attach()
    }

    private fun getVIewPagerSwitchRoutine(): Runnable =
        Runnable {
            if (page == numPages - 1) {
                page = 0;
            } else {
                page++;
            }
            viewBinding.viewPager.setCurrentItem(page, true);
            //handler.postDelayed(this, delay);
        }

}
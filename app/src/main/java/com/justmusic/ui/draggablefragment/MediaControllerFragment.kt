/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.ui.draggablefragment

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.view.View
import com.justmusic.MainActivity
import com.justmusic.R
import com.justmusic.base.BaseFragment
import com.justmusic.databinding.FragmentMediaControllerBinding
import com.justmusic.utils.extensions.loadImage
import dagger.hilt.android.AndroidEntryPoint

/**
 *Draggable player controller fragment
 */
@AndroidEntryPoint
class MediaControllerFragment : BaseFragment(R.layout.fragment_media_controller) {

    private var mIsPlaying = false
    private var _binding: FragmentMediaControllerBinding? = null
    val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediaControllerBinding.bind(view)
        binding.rootLayout.transitionToEnd()
        initListener()
    }

    private fun initListener() {
        binding.imageViewPlayPause.setOnClickListener { (requireActivity() as MainActivity).mediaPlayerManager.playPause() }
        binding.imageViewPlayPrevSong.setOnClickListener { (requireActivity() as MainActivity).mediaPlayerManager.playPrevious() }
        binding.imageViewPlayNextSong.setOnClickListener { (requireActivity() as MainActivity).mediaPlayerManager.playNext() }
    }

    fun setIsPlaying(isPlaying: Boolean) {
        if (isPlaying) {
            binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white)
        } else {
            binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white)
        }
        mIsPlaying = isPlaying
    }

    fun setMediaTitle(mediaItem: MediaMetadataCompat) {
        binding.imageViewItem.loadImage(mediaItem.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI))
        binding.txTitleMaxWindow.text = mediaItem.description.title
        binding.txSubTitleMaxWindow.text =
            mediaItem.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

        binding.txTitleMinWindow.text =
            mediaItem.description.title
        binding.txSubTitleMinWindow.text =
            mediaItem.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    }

    override fun showLoading() {}

    override fun hideLoading() {}

    override fun onRetry() {}

    companion object {
        const val TAG = "MediaControllerFragment"
        fun newInstance() = MediaControllerFragment()
    }
}

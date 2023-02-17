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
package com.justmusic.base

import android.animation.ObjectAnimator
import android.support.v4.media.MediaMetadataCompat
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.justmusic.MainActivity
import com.justmusic.R
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.shared.HttpStatusEnum
import com.justmusic.ui.search.fragments.SearchFragment
import com.justmusic.ui.song.adapter.SongsAdapter
import com.justmusic.utils.extensions.hideKeyboard
import com.justmusic.utils.extensions.showSingleButtonDialog
import com.justmusic.utils.extensions.showSnackBar
import com.justmusic.utils.mapper.toMediaMetadataCompat
import dagger.hilt.android.AndroidEntryPoint

/**
 * This is a BaseFragment can be use show/hide progress dialog while API call
 * Defined method to handle errors from API
 * @param id Required to pass layout id
 */
@AndroidEntryPoint
abstract class BaseFragment(id: Int) : Fragment(id) {
    /**
     * This method is use to show loading in whole app like in dialog
     * or can be override in particular screen if needs to change dialog behaviour
     * */
    abstract fun showLoading()

    /**
     * This method is used to hide progress or loader whenever http request finished.
     * */
    abstract fun hideLoading()

    /**
     * This method is used to retry in case internet not available and reconnected.
     * */
    abstract fun onRetry()

    /**
     * This method is used to handle error type of http based on custom type of exception
     * */
    open fun showError(error: String, httpStatusEnum: HttpStatusEnum) {
        when (httpStatusEnum) {
            HttpStatusEnum.NO_INTERNET -> (onNoInternetConnection())
            HttpStatusEnum.BAD_REQUEST -> (onNoDataFound())
            HttpStatusEnum.UN_AUTHORIZATION -> (onAuthorizationExpired())
            else -> {
                onSomethingWentWrong()
            }
        }
    }

    /** This method is used to handle error type of http error or any general error  */
    private fun onSomethingWentWrong() {
        requireActivity().showSnackBar(getString(R.string.something_went_wrong))
    }

    /** This method is used to handle error http 401 for session expired */
    private fun onAuthorizationExpired() {
    }

    /** This method is used to handle error http 404 for no resource found */
    @Suppress("unused")
    private fun onNoResourceFound(message: String?) {
        message?.let { requireActivity().showSnackBar(it) }
    }

    /** This method is used to handle no internet connection */
    open fun onNoInternetConnection() {
        requireActivity().showSnackBar(getString(R.string.no_internet_description))
    }

    /** This method is used to handle no data */
    private fun onNoDataFound() {
    }

    /** This method is used to animated view as bounce effect*/
    fun bonceAnimationForView(view: View) {
        val animY = ObjectAnimator.ofFloat(view, "translationY", -100f, 0f)
        animY.duration = 300
        animY.interpolator = BounceInterpolator()
        animY.start()
    }

    fun setToolbarTitle(textView: TextView, title: String) {
        textView.text = title
    }

    fun disableSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false
    }

    private fun getCurrentFragmentFromNavigation(): Fragment? {
        val navHostFragment: Fragment? =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    fun notifyParentFragmentItemAddedInRecentSearch(itemId: Long, appContentTypeEnum: AppContentTypeEnum) {
        val fragment = getCurrentFragmentFromNavigation()
        fragment?.let {
            (fragment as SearchFragment).recentSearchHelper.addRecentSearch(itemId, appContentTypeEnum)
        }
    }

    fun handleSongClickEvent(position: Int, songContent: SongContent, songsAdapter: SongsAdapter) {
        view?.hideKeyboard()
        val mainActivity = requireActivity() as MainActivity
        val listOfMedia = mutableListOf<MediaMetadataCompat>()
        songsAdapter.listOfSongsContent.map {
            listOfMedia.add(it.toMediaMetadataCompat())
        }
        mainActivity.mediaPlayerManager.justMusicApplication?.setMediaItems(listOfMedia)
        mainActivity.mediaPlayerManager.onMediaSelected(
            songContent.id.toString(),
            songContent.toMediaMetadataCompat(),
            position
        )
        mainActivity.mediaPlayerManager.preferencesHelper?.savePlaylistId(songContent.id.toString())
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleOnAlbumClicked(position: Int, albumContent: AlbumContent) {
        requireContext().showSingleButtonDialog()
    }
}

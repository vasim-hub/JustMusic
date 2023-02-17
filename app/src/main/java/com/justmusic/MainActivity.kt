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
package com.justmusic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.justmusic.cache.local.PreferencesHelper
import com.justmusic.databinding.ActivityMainBinding
import com.justmusic.musicplayer.MediaPlayerManager
import com.justmusic.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity which is used to start application
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mediaPlayerManager: MediaPlayerManager

    @Inject lateinit var preferencesHelper: PreferencesHelper
    @Inject lateinit var justMusicApplication: JustMusicApplication

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerBottomNavigationEventListener()
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        mediaPlayerManager = MediaPlayerManager(this, binding, justMusicApplication, preferencesHelper)
    }

    private fun registerBottomNavigationEventListener() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setOnItemSelectedListener { item ->
            navController.navigate(item.itemId)
            true
        }
    }

    public override fun onStart() {
        super.onStart()
        mediaPlayerManager.onStart()
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStop() {
        super.onStop()
        mediaPlayerManager.onStop()
    }

    override fun onBackPressed() {
        if (mediaPlayerManager.isPlayerExpanded()) return
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        showSnackBar(getString(R.string.message_back_button))
        Handler(Looper.getMainLooper()).postDelayed(
            {
                doubleBackToExitPressedOnce = false
            },
            2000
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
